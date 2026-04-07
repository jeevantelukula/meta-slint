inherit cargo
inherit pkgconfig

SRC_URI = "git://github.com/slint-ui/slint.git;protocol=https;branch=master;name=slint"
SRCREV_slint = "${AUTOREV}"
SRC_URI += "file://0001-WIP-v-1-14-0-Use-a-patched-gettext-to-avoid-cross-compiling-g.patch"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=093007ec281bbdeea447b0040b01a74d"

# ---------------------------------------------------------------------------
# SLINT_RENDERER: controls renderer selection, Skia fetching, and dependencies.
#
# "skia"     - Skia OpenGL renderer (GPU required: mesa-pvr or equivalent).
#              All 13 Skia third-party deps are pre-fetched as HTTPS archives.
# "software" - Slint built-in CPU renderer (no GPU required).
#              No Skia deps fetched; significantly faster do_fetch + do_configure.
#
# Override in your layer's bbappend for non-GPU machines:
#   SLINT_RENDERER:mymachine = "software"
# ---------------------------------------------------------------------------
SLINT_RENDERER ?= "skia"

# skia-bindings 0.90.0 needs tag m142-0.89.1 from rust-skia/skia
SKIA_COMMIT = "d6b5e2f8677dfcbecb882cc1237b5d8a73e45c56"

# Skia source + all 13 third-party deps - only fetched when using renderer-skia.
# 11 repos: HTTPS archives (GitHub + gitiles) - checksummed, offline after first fetch.
# 2 repos (freetype2, zlib): git:// via Bitbake fetcher - chromium/src/third_party/*
#   gitiles archive throttles automated HTTPS access; Bitbake git fetcher is sequential
#   and caches result as git2_*.tar.gz in DL_DIR for fully offline subsequent builds.
SKIA_SRC_URIS = " \
    https://github.com/rust-skia/skia/archive/${SKIA_COMMIT}.tar.gz;downloadfilename=skia-source-${SKIA_COMMIT}.tar.gz;name=skia;unpack=0 \
    https://github.com/google/brotli/archive/6d03dfbedda1615c4cba1211f8d81735575209c8.tar.gz;name=skia-brotli;unpack=0;downloadfilename=skia-brotli.tar.gz \
    https://github.com/GPUOpen-LibrariesAndSDKs/D3D12MemoryAllocator/archive/169895d529dfce00390a20e69c2f516066fe7a3b.tar.gz;name=skia-d3d12;unpack=0;downloadfilename=skia-d3d12.tar.gz \
    https://github.com/libexpat/libexpat/archive/8e49998f003d693213b538ef765814c7d21abada.tar.gz;name=skia-expat;unpack=0;downloadfilename=skia-expat.tar.gz \
    https://github.com/harfbuzz/harfbuzz/archive/08b52ae2e44931eef163dbad71697f911fadc323.tar.gz;name=skia-harfbuzz;unpack=0;downloadfilename=skia-harfbuzz.tar.gz \
    https://github.com/webmproject/libwebp/archive/845d5476a866141ba35ac133f856fa62f0b7445f.tar.gz;name=skia-libwebp;unpack=0;downloadfilename=skia-libwebp.tar.gz \
    https://github.com/GPUOpen-LibrariesAndSDKs/VulkanMemoryAllocator/archive/a6bfc237255a6bac1513f7c1ebde6d8aed6b5191.tar.gz;name=skia-vma;unpack=0;downloadfilename=skia-vma.tar.gz \
    https://github.com/KhronosGroup/SPIRV-Cross/archive/b8fcf307f1f347089e3c46eb4451d27f32ebc8d3.tar.gz;name=skia-spirvcross;unpack=0;downloadfilename=skia-spirvcross.tar.gz \
    https://github.com/google/wuffs-mirror-release-c/archive/e3f919ccfe3ef542cfc983a82146070258fb57f8.tar.gz;name=skia-wuffs;unpack=0;downloadfilename=skia-wuffs.tar.gz \
    https://chromium.googlesource.com/chromium/deps/icu/+archive/364118a1d9da24bb5b770ac3d762ac144d6da5a4.tar.gz;name=skia-icu;unpack=0;downloadfilename=skia-icu.tar.gz \
    https://chromium.googlesource.com/chromium/deps/libjpeg_turbo/+archive/e14cbfaa85529d47f9f55b0f104a579c1061f9ad.tar.gz;name=skia-libjpeg;unpack=0;downloadfilename=skia-libjpeg.tar.gz \
    https://skia.googlesource.com/third_party/libpng/+archive/ed217e3e601d8e462f7fd1e04bed43ac42212429.tar.gz;name=skia-libpng;unpack=0;downloadfilename=skia-libpng.tar.gz \
    git://chromium.googlesource.com/chromium/src/third_party/freetype2.git;protocol=https;nobranch=1;name=skia-freetype;destsuffix=skia-externals/freetype \
    git://chromium.googlesource.com/chromium/src/third_party/zlib;protocol=https;nobranch=1;name=skia-zlib;destsuffix=skia-externals/zlib \
"

# Only add Skia URIs when using the Skia renderer
SRC_URI += "${@d.getVar('SKIA_SRC_URIS') if d.getVar('SLINT_RENDERER') == 'skia' else ''}"

SRCREV_skia-freetype = "1518bc83d26b434031bd12c706ac3c7dab3902fd"
SRCREV_skia-zlib = "646b7f569718921d7d4b5b8e22572ff6c76f2596"

# With multiple git:// SCMs, SRCREV_FORMAT tells Bitbake which revision to use
# for the package version string. Use only the main Slint repo (not Skia deps).
SRCREV_FORMAT = "slint"

SRC_URI[skia.sha256sum] = "a13007290588a3810a68d9a3d476ca766904d16a4c2ab4803b759659ecd555d2"
SRC_URI[skia-brotli.sha256sum] = "0e8eea905081ce894d1616970a83b21265a13505ce06e8aa6a747fd686938d10"
SRC_URI[skia-d3d12.sha256sum] = "145fd395dc97bd57dab7763f68a97de7ce745eb8d862c1b875b6f9093cff4e19"
SRC_URI[skia-expat.sha256sum] = "3446069dd728cb6557649541e78d44611c7fbdb21317c33e4b69773b930e5ed3"
SRC_URI[skia-harfbuzz.sha256sum] = "9e01ab70eec885ece4306effb68e5ab413e8dcf5e498a0a80ae0fd161753da3a"
SRC_URI[skia-icu.sha256sum] = "bb316e35e196805125a2255fde41e350d9559988bd1cf55024d178b47370e452"
SRC_URI[skia-libjpeg.sha256sum] = "2867910620da31ee6dd3b706c7f47bd3a5cd95868cffd1678df7d163a81556fd"
SRC_URI[skia-libpng.sha256sum] = "c0e6fbd692b659f744a5e635d216d658e4e99e82673de57505d9d6eb07f87023"
SRC_URI[skia-libwebp.sha256sum] = "179ed07094681293cd9f7f64b55d88833862fe37601fe13a0eba8ee676f2a0bd"
SRC_URI[skia-spirvcross.sha256sum] = "4793754d72c89920321e4cf4576a8edab211d46e69309a827a95ebda4deb8881"
SRC_URI[skia-vma.sha256sum] = "7444a78beeb01ee3baca58721b2b7c936521f05df1f6e322bbafe3c2d4114704"
SRC_URI[skia-wuffs.sha256sum] = "e849dab1f372f16b782ba7528e7f70281e35425bd2d644c92a36fb95909f98db"

SUMMARY = "Various Rust-based demos of Slint packaged up in /usr/bin"
DESCRIPTION = "This recipe builds various Slint demos such as the energy monitor \
or the printer demo and installs the binaries into /usr/bin."
HOMEPAGE = "https://slint.dev/"
LICENSE = "GPL-3.0-only | Slint-Commercial"

# QA exceptions for GitHub archive URLs and embedded build paths (Rust/Skia artifacts)
ERROR_QA:remove = "src-uri-bad"
WARN_QA:append = " src-uri-bad"
INSANE_SKIP:${PN} += "buildpaths"

inherit slint_common
inherit features_check

PV = "git-${SRCPV}"

# OpenGL distro feature and GPU-specific deps only needed for Skia renderer
REQUIRED_DISTRO_FEATURES:append:class-target = "${@'opengl' if d.getVar('SLINT_RENDERER') == 'skia' else ''}"

# Common deps for all renderers (KMS display backend needs gbm/egl regardless of renderer)
# virtual/libgbm: GBM for KMS scanout buffer allocation (linuxkms backend)
# virtual/egl: EGL surface via glutin for KMS output (linuxkms backend)
DEPENDS:append:class-target = " fontconfig libxkbcommon libdrm seatd udev libinput virtual/libgbm virtual/egl"
DEPENDS:append:class-target = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxcb', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
"

# GPU/Skia-specific deps - only when using renderer-skia
DEPENDS:append:class-target = "${@' virtual/libgl clang-cross-${TARGET_ARCH} ca-certificates-native curl-native gn-native ninja-native' if d.getVar('SLINT_RENDERER') == 'skia' else ''}"

RDEPENDS:${PN}:class-target += "xkeyboard-config"

CARGO_DISABLE_BITBAKE_VENDORING = "1"

CARGO_BUILD_FLAGS = "-v --target ${RUST_HOST_SYS} ${BUILD_MODE} --manifest-path=${CARGO_MANIFEST_PATH} -p slint --features 'slint/backend-linuxkms slint/renderer-${SLINT_RENDERER}'"

# Increase network timeouts for slow networks: downloading 344+ crates can take many minutes
export CARGO_HTTP_TIMEOUT = "3600"
export CARGO_NET_RETRY = "10"
export CARGO_HTTP_MULTIPLIER = "2"

# do_configure is fully offline (all deps pre-fetched in do_fetch)
# do_compile needs network for cargo crate downloads
do_compile[network] = "1"

BBCLASSEXTEND = "native"

do_configure:append() {
    SKIA_PREP_DIR="${UNPACKDIR}/skia-source"

    # Skia source preparation is only needed for renderer-skia
    if [ "${SLINT_RENDERER}" = "skia" ]; then
        if [ ! -f "${SKIA_PREP_DIR}/.skia-deps-synced" ]; then
            bbnote "Preparing Skia source with pre-fetched dependencies..."

            # Extract Skia source from bitbake-downloaded tarball
            rm -rf ${SKIA_PREP_DIR}
            tar -xzf ${DL_DIR}/skia-source-${SKIA_COMMIT}.tar.gz -C ${UNPACKDIR}
            mv ${UNPACKDIR}/skia-${SKIA_COMMIT} ${SKIA_PREP_DIR}

            # Place native gn binary where Skia expects it
            mkdir -p ${SKIA_PREP_DIR}/bin
            mkdir -p ${SKIA_PREP_DIR}/third_party/gn
            cp ${STAGING_BINDIR_NATIVE}/gn ${SKIA_PREP_DIR}/bin/gn
            chmod +x ${SKIA_PREP_DIR}/bin/gn
            cp ${STAGING_BINDIR_NATIVE}/gn ${SKIA_PREP_DIR}/third_party/gn/gn
            chmod +x ${SKIA_PREP_DIR}/third_party/gn/gn

            # Replace fetch-gn with a no-op (gn already provided by gn-native)
            cat > ${SKIA_PREP_DIR}/bin/fetch-gn << 'FETCHGN'
#!/usr/bin/env python3
import sys
sys.exit(0)
FETCHGN
            chmod +x ${SKIA_PREP_DIR}/bin/fetch-gn

            # Extract pre-fetched HTTPS archive deps into third_party/externals/.
            # GitHub archives have a top-level REPO-COMMIT/ dir (strip with --strip-components=1).
            # Gitiles +archive tarballs have no top-level dir (extract directly).
            mkdir -p ${SKIA_PREP_DIR}/third_party/externals

            mkdir -p ${SKIA_PREP_DIR}/third_party/externals/brotli
            tar -xzf ${DL_DIR}/skia-brotli.tar.gz --strip-components=1 -C ${SKIA_PREP_DIR}/third_party/externals/brotli/

            mkdir -p ${SKIA_PREP_DIR}/third_party/externals/d3d12allocator
            tar -xzf ${DL_DIR}/skia-d3d12.tar.gz --strip-components=1 -C ${SKIA_PREP_DIR}/third_party/externals/d3d12allocator/

            mkdir -p ${SKIA_PREP_DIR}/third_party/externals/expat
            tar -xzf ${DL_DIR}/skia-expat.tar.gz --strip-components=1 -C ${SKIA_PREP_DIR}/third_party/externals/expat/

            mkdir -p ${SKIA_PREP_DIR}/third_party/externals/harfbuzz
            tar -xzf ${DL_DIR}/skia-harfbuzz.tar.gz --strip-components=1 -C ${SKIA_PREP_DIR}/third_party/externals/harfbuzz/

            mkdir -p ${SKIA_PREP_DIR}/third_party/externals/libwebp
            tar -xzf ${DL_DIR}/skia-libwebp.tar.gz --strip-components=1 -C ${SKIA_PREP_DIR}/third_party/externals/libwebp/

            mkdir -p ${SKIA_PREP_DIR}/third_party/externals/vulkanmemoryallocator
            tar -xzf ${DL_DIR}/skia-vma.tar.gz --strip-components=1 -C ${SKIA_PREP_DIR}/third_party/externals/vulkanmemoryallocator/

            mkdir -p ${SKIA_PREP_DIR}/third_party/externals/spirv-cross
            tar -xzf ${DL_DIR}/skia-spirvcross.tar.gz --strip-components=1 -C ${SKIA_PREP_DIR}/third_party/externals/spirv-cross/

            mkdir -p ${SKIA_PREP_DIR}/third_party/externals/wuffs
            tar -xzf ${DL_DIR}/skia-wuffs.tar.gz --strip-components=1 -C ${SKIA_PREP_DIR}/third_party/externals/wuffs/

            mkdir -p ${SKIA_PREP_DIR}/third_party/externals/icu
            tar -xzf ${DL_DIR}/skia-icu.tar.gz -C ${SKIA_PREP_DIR}/third_party/externals/icu/

            mkdir -p ${SKIA_PREP_DIR}/third_party/externals/libjpeg-turbo
            tar -xzf ${DL_DIR}/skia-libjpeg.tar.gz -C ${SKIA_PREP_DIR}/third_party/externals/libjpeg-turbo/

            mkdir -p ${SKIA_PREP_DIR}/third_party/externals/libpng
            tar -xzf ${DL_DIR}/skia-libpng.tar.gz -C ${SKIA_PREP_DIR}/third_party/externals/libpng/

            # freetype and zlib fetched via git:// SRC_URI into destsuffix by Bitbake
            cp -a ${UNPACKDIR}/skia-externals/freetype ${SKIA_PREP_DIR}/third_party/externals/freetype
            cp -a ${UNPACKDIR}/skia-externals/zlib ${SKIA_PREP_DIR}/third_party/externals/zlib

            touch ${SKIA_PREP_DIR}/.skia-deps-synced
            bbnote "Skia source prepared with all dependencies (offline, no network)"
        fi
    fi
}

do_compile:prepend() {
    # Set CA bundle for both curl-based (CURL_CA_BUNDLE) and OpenSSL-based
    # (SSL_CERT_FILE) HTTPS in cargo. Required on build servers with corporate
    # proxies that inject their own TLS certificates.
    CURL_CA_BUNDLE=${STAGING_DIR_NATIVE}/etc/ssl/certs/ca-certificates.crt
    export CURL_CA_BUNDLE
    export SSL_CERT_FILE=${STAGING_DIR_NATIVE}/etc/ssl/certs/ca-certificates.crt

    if [ "${SLINT_RENDERER}" = "skia" ]; then
        export SKIA_SOURCE_DIR="${UNPACKDIR}/skia-source"
        export SKIA_GN_COMMAND="${STAGING_BINDIR_NATIVE}/gn"
        export SKIA_NINJA_COMMAND="${STAGING_BINDIR_NATIVE}/ninja"
    fi
}

do_compile:append() {
    # Reduce RAM requirements
    export CARGO_PROFILE_RELEASE_LTO=false
    for p in slide_puzzle printerdemo gallery opengl_texture opengl_underlay energy-monitor home-automation; do
        cargo build ${CARGO_BUILD_FLAGS} -p $p
    done
    rm -f "${B}/target/${CARGO_TARGET_SUBDIR}/"*.so
    rm -f "${B}/target/${CARGO_TARGET_SUBDIR}/"*.rlib
}
