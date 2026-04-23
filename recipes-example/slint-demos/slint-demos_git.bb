inherit cargo
inherit pkgconfig

SRC_URI = "git://github.com/slint-ui/slint.git;protocol=https;branch=master;rev=master"
SRC_URI += "file://0001-WIP-v-1-14-0-Use-a-patched-gettext-to-avoid-cross-compiling-g.patch"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=093007ec281bbdeea447b0040b01a74d"

SUMMARY = "Various Rust-based demos of Slint packaged up in /usr/bin"
DESCRIPTION = "This recipe builds various Slint demos such as the energy monitor \
or the printer demo and installs the binaries into /usr/bin."
HOMEPAGE = "https://slint.dev/"
LICENSE = "GPL-3.0-only | Slint-Commercial"

inherit slint_common
inherit features_check

PV = "git-${SRCPV}"

REQUIRED_DISTRO_FEATURES:append:class-target = "opengl"

DEPENDS:append:class-target = " fontconfig libxkbcommon virtual/libgl"
DEPENDS:append:class-target = " clang-cross-${TARGET_ARCH} ca-certificates-native curl-native ninja-native"
DEPENDS:append:class-target = " libdrm virtual/egl virtual/libgbm seatd udev libinput"
DEPENDS:append:class-target = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxcb', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
"
RDEPENDS:${PN}:class-target += "xkeyboard-config"

CARGO_DISABLE_BITBAKE_VENDORING = "1"
CARGO_BUILD_FLAGS = "-v --target ${RUST_HOST_SYS} ${BUILD_MODE} --manifest-path=${CARGO_MANIFEST_PATH}"
# Wire CARGO_FEATURES into the cargo invocation. OE-core's cargo.bbclass only
# passes features via PACKAGECONFIG_CONFARGS (empty here, no PACKAGECONFIG
# entries) and never reads CARGO_FEATURES directly. Appending --features here
# ensures every per-demo 'cargo build -p $p' call in do_compile:append
# respects the feature set.
CARGO_BUILD_FLAGS:append = " ${@'--features ' + ','.join(d.getVar('CARGO_FEATURES').split()) if d.getVar('CARGO_FEATURES') else ''}"

do_configure[network] = "1"
do_compile[network] = "1"


BBCLASSEXTEND = "native"

CARGO_FEATURES = "slint/backend-linuxkms slint/renderer-skia"

# Full list of demos to build. Downstream layers can override per machine to
# add or remove entries based on hardware capabilities. For example, platforms
# without a GPU should exclude the OpenGL-specific demos (opengl_texture,
# opengl_underlay) which require hardware OpenGL at runtime even though they
# compile with any renderer.
SLINT_DEMOS = "slide_puzzle printerdemo gallery opengl_texture opengl_underlay energy-monitor home-automation"

# Override the workspace build from cargo.bbclass. Without this, oe_cargo_build
# runs 'cargo build' with no -p flag, which builds ALL Slint workspace
# default-members (including internal/backends/winit and internal/backends/selector).
# Those backend crates depend on i-slint-renderer-skia with their OWN default
# features — pulling in skia-bindings regardless of the top-level --features flag
# passed for the slint crate. The per-demo builds in do_compile:append already
# build every needed binary with the correct -p and --features flags; the
# workspace build is redundant and harmful for machines without a GPU.
cargo_do_compile() {
    :
}

do_compile:prepend() {
    CURL_CA_BUNDLE=${STAGING_DIR_NATIVE}/etc/ssl/certs/ca-certificates.crt
    export CURL_CA_BUNDLE
    # Use the git protocol for the crates.io index instead of sparse HTTP
    # so cargo doesn't open hundreds of HTTP/1.1 connections at once
    # (OE-core's curl-native is built without HTTP/2 -- see commit message).
    export CARGO_REGISTRIES_CRATES_IO_PROTOCOL=git
    export CARGO_HTTP_TIMEOUT=120
    export CARGO_NET_RETRY=5
    # Trim build-time absolute paths from all binaries (stabilised in Rust 1.73).
    # Demos such as gallery and printerdemo call slint::init_translations! which
    # uses include_dir! internally. include_dir! embeds the absolute source path
    # of the lang/ directory (derived from env!("CARGO_MANIFEST_DIR")) as a
    # static string in .rodata. --remap-path-prefix cannot fix this because it
    # only covers file!() macro and DWARF debug info, not env!() expansions.
    # CARGO_PROFILE_RELEASE_TRIM_PATHS=all strips the package root prefix from
    # every embedded path string including those from env!() macro expansions,
    # leaving a relative path (e.g. "lang/") with no TMPDIR reference.
    # This also improves binary reproducibility across machines.
    export CARGO_PROFILE_RELEASE_TRIM_PATHS=all
}
do_compile:append() {
    # Reduce RAM requirements
    export CARGO_PROFILE_RELEASE_LTO=false
    for p in ${SLINT_DEMOS}; do
        cargo build ${CARGO_BUILD_FLAGS} -p $p
    done
    rm -f "${B}/target/${CARGO_TARGET_SUBDIR}"/*.so
    rm -f "${B}/target/${CARGO_TARGET_SUBDIR}"/*.rlib
}
