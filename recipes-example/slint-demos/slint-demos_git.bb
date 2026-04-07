inherit cargo
inherit pkgconfig

SRC_URI = "git://github.com/slint-ui/slint.git;protocol=https;branch=master"
SRCREV = "${AUTOREV}"
SRC_URI += "file://0001-WIP-v-1-14-0-Use-a-patched-gettext-to-avoid-cross-compiling-g.patch"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=093007ec281bbdeea447b0040b01a74d"

# skia-bindings 0.90.0 needs tag m142-0.89.1 from rust-skia/skia - fetch via commit hash
SKIA_COMMIT = "d6b5e2f8677dfcbecb882cc1237b5d8a73e45c56"

# Pre-download Skia source via bitbake's fetcher (proxy/mirror aware, cached)
SRC_URI += "https://github.com/rust-skia/skia/archive/${SKIA_COMMIT}.tar.gz;downloadfilename=skia-source-${SKIA_COMMIT}.tar.gz;name=skia;unpack=0"
SRC_URI[skia.sha256sum] = "a13007290588a3810a68d9a3d476ca766904d16a4c2ab4803b759659ecd555d2"

# ---------------------------------------------------------------------------
# Skia third-party dependencies pre-fetched as HTTPS archives via do_fetch.
#
# This replaces git-sync-deps network operations in do_configure with proper
# Yocto fetching: downloads happen before the build sandbox, are cached in
# DL_DIR (shared/offline after first fetch), use HTTPS not git protocol
# (avoids GnuTLS/HTTP2 issues), and are fully reproducible (pinned commits).
#
# 11 repos: HTTPS archives from GitHub/gitiles (deterministic, checksummed)
# 2 repos (freetype2, zlib): git:// SRC_URI via Bitbake fetcher - chromium's
#   /src/third_party/ gitiles archive endpoint blocks automated HTTPS access;
#   Bitbake's git fetcher handles these sequentially with caching.
# ---------------------------------------------------------------------------

# GitHub-hosted repos (archive = exact tree at pinned commit, no git history)
SRC_URI += "https://github.com/google/brotli/archive/6d03dfbedda1615c4cba1211f8d81735575209c8.tar.gz;name=skia-brotli;unpack=0;downloadfilename=skia-brotli.tar.gz"
SRC_URI += "https://github.com/GPUOpen-LibrariesAndSDKs/D3D12MemoryAllocator/archive/169895d529dfce00390a20e69c2f516066fe7a3b.tar.gz;name=skia-d3d12;unpack=0;downloadfilename=skia-d3d12.tar.gz"
SRC_URI += "https://github.com/libexpat/libexpat/archive/8e49998f003d693213b538ef765814c7d21abada.tar.gz;name=skia-expat;unpack=0;downloadfilename=skia-expat.tar.gz"
SRC_URI += "https://github.com/harfbuzz/harfbuzz/archive/08b52ae2e44931eef163dbad71697f911fadc323.tar.gz;name=skia-harfbuzz;unpack=0;downloadfilename=skia-harfbuzz.tar.gz"
SRC_URI += "https://github.com/webmproject/libwebp/archive/845d5476a866141ba35ac133f856fa62f0b7445f.tar.gz;name=skia-libwebp;unpack=0;downloadfilename=skia-libwebp.tar.gz"
SRC_URI += "https://github.com/GPUOpen-LibrariesAndSDKs/VulkanMemoryAllocator/archive/a6bfc237255a6bac1513f7c1ebde6d8aed6b5191.tar.gz;name=skia-vma;unpack=0;downloadfilename=skia-vma.tar.gz"
SRC_URI += "https://github.com/KhronosGroup/SPIRV-Cross/archive/b8fcf307f1f347089e3c46eb4451d27f32ebc8d3.tar.gz;name=skia-spirvcross;unpack=0;downloadfilename=skia-spirvcross.tar.gz"
SRC_URI += "https://github.com/google/wuffs-mirror-release-c/archive/e3f919ccfe3ef542cfc983a82146070258fb57f8.tar.gz;name=skia-wuffs;unpack=0;downloadfilename=skia-wuffs.tar.gz"

# Chromium/Skia gitiles repos (accessible via +archive endpoint)
SRC_URI += "https://chromium.googlesource.com/chromium/deps/icu/+archive/364118a1d9da24bb5b770ac3d762ac144d6da5a4.tar.gz;name=skia-icu;unpack=0;downloadfilename=skia-icu.tar.gz"
SRC_URI += "https://chromium.googlesource.com/chromium/deps/libjpeg_turbo/+archive/e14cbfaa85529d47f9f55b0f104a579c1061f9ad.tar.gz;name=skia-libjpeg;unpack=0;downloadfilename=skia-libjpeg.tar.gz"
SRC_URI += "https://skia.googlesource.com/third_party/libpng/+archive/ed217e3e601d8e462f7fd1e04bed43ac42212429.tar.gz;name=skia-libpng;unpack=0;downloadfilename=skia-libpng.tar.gz"

# chromium/src/third_party/* repos: gitiles archive endpoint returns HTTP 200
# on build servers but throttles rapid automated downloads; fetched via
# Bitbake's git fetcher (sequential, cached as git2_*.tar.gz in DL_DIR).
SRC_URI += "git://chromium.googlesource.com/chromium/src/third_party/freetype2.git;protocol=https;nobranch=1;name=skia-freetype;destsuffix=skia-externals/freetype"
SRCREV_skia-freetype = "1518bc83d26b434031bd12c706ac3c7dab3902fd"
SRC_URI += "git://chromium.googlesource.com/chromium/src/third_party/zlib;protocol=https;nobranch=1;name=skia-zlib;destsuffix=skia-externals/zlib"
SRCREV_skia-zlib = "646b7f569718921d7d4b5b8e22572ff6c76f2596"

SRC_URI[skia-brotli.sha256sum] = "0e8eea905081ce894d1616970a83b21265a13505ce06e8aa6a747fd686938d10"
SRC_URI[skia-d3d12.sha256sum] = "145fd395dc97bd57dab7763f68a97de7ce745eb8d862c1b875b6f9093cff4e19"
SRC_URI[skia-expat.sha256sum] = "034529019ca8d367f39bba6ecda5e72d18b0c212d5ed2228b6b699ff78ee2475"
SRC_URI[skia-harfbuzz.sha256sum] = "f010d1619b8d2e88c32949e84144cf79c80d181e2fb819380477c68bbf78f32b"
SRC_URI[skia-libwebp.sha256sum] = "aa48ed7bb0cbd3a81d6d6d5cb7fcbb1612a3c9d918173cebeab33963382fbfdd"
SRC_URI[skia-vma.sha256sum] = "7444a78beeb01ee3baca58721b2b7c936521f05df1f6e322bbafe3c2d4114704"
SRC_URI[skia-spirvcross.sha256sum] = "4793754d72c89920321e4cf4576a8edab211d46e69309a827a95ebda4deb8881"
SRC_URI[skia-wuffs.sha256sum] = "e849dab1f372f16b782ba7528e7f70281e35425bd2d644c92a36fb95909f98db"
SRC_URI[skia-icu.sha256sum] = "34432026d3797dc7883309776ddc71a3b1dd041a2188ddeda666d11f6eb6c871"
SRC_URI[skia-libjpeg.sha256sum] = "73af7beb2bc948fda35d0d3bb14885cd5e9200c1f3b3bc7d230bff0e38709904"
SRC_URI[skia-libpng.sha256sum] = "c0ae2e82c491c557b0a526b3445d9381347310e0de39e8c7141f4d145fee5e91"

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

REQUIRED_DISTRO_FEATURES:append:class-target = "opengl"

DEPENDS:append:class-target = " fontconfig libxkbcommon virtual/libgl"
DEPENDS:append:class-target = " clang-cross-${TARGET_ARCH} ca-certificates-native curl-native"
DEPENDS:append:class-target = " libdrm virtual/egl virtual/libgbm seatd udev libinput"
DEPENDS:append:class-target = " gn-native ninja-native"
RDEPENDS:${PN}:class-target += "xkeyboard-config"

CARGO_DISABLE_BITBAKE_VENDORING = "1"

CARGO_BUILD_FLAGS = "-v --target ${RUST_HOST_SYS} ${BUILD_MODE} --manifest-path=${CARGO_MANIFEST_PATH} -p slint --features 'slint/backend-linuxkms slint/renderer-skia'"

# Increase network timeouts for slow networks: downloading 344+ crates can take many minutes
export CARGO_HTTP_TIMEOUT = "3600"
export CARGO_NET_RETRY = "10"
export CARGO_HTTP_MULTIPLIER = "2"

# do_configure no longer needs network (all Skia deps fetched in do_fetch)
# do_compile still needs network for cargo crate downloads
do_compile[network] = "1"

BBCLASSEXTEND = "native"

# Prepare Skia source directory using pre-fetched dependency archives.
# All 13 Skia third-party deps were downloaded in do_fetch via SRC_URI.
# do_configure simply extracts them to the right locations - no network needed.
do_configure:append() {
    SKIA_PREP_DIR="${UNPACKDIR}/skia-source"

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

        # Extract 11 pre-fetched HTTPS archive deps into third_party/externals/.
        # GitHub archives have a top-level directory (strip with --strip-components=1).
        # Gitiles +archive tarballs have no top-level dir (extract directly).
        mkdir -p ${SKIA_PREP_DIR}/third_party/externals

        # GitHub archives (strip top-level REPO-COMMIT/ directory)
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

        # Gitiles +archive tarballs (no top-level directory, extract directly)
        mkdir -p ${SKIA_PREP_DIR}/third_party/externals/icu
        tar -xzf ${DL_DIR}/skia-icu.tar.gz -C ${SKIA_PREP_DIR}/third_party/externals/icu/

        mkdir -p ${SKIA_PREP_DIR}/third_party/externals/libjpeg-turbo
        tar -xzf ${DL_DIR}/skia-libjpeg.tar.gz -C ${SKIA_PREP_DIR}/third_party/externals/libjpeg-turbo/

        mkdir -p ${SKIA_PREP_DIR}/third_party/externals/libpng
        tar -xzf ${DL_DIR}/skia-libpng.tar.gz -C ${SKIA_PREP_DIR}/third_party/externals/libpng/

        # freetype and zlib were fetched via git:// SRC_URI into UNPACKDIR by Bitbake.
        # Move them from where Bitbake unpacked them into the externals directory.
        if [ -d "${UNPACKDIR}/skia-externals/freetype" ]; then
            cp -a ${UNPACKDIR}/skia-externals/freetype ${SKIA_PREP_DIR}/third_party/externals/freetype
        else
            bbfatal "freetype not found - check git:// SRC_URI fetch succeeded"
        fi

        if [ -d "${UNPACKDIR}/skia-externals/zlib" ]; then
            cp -a ${UNPACKDIR}/skia-externals/zlib ${SKIA_PREP_DIR}/third_party/externals/zlib
        else
            bbfatal "zlib not found - check git:// SRC_URI fetch succeeded"
        fi

        touch ${SKIA_PREP_DIR}/.skia-deps-synced
        bbnote "Skia source prepared with all dependencies (no network required)"
    fi
}

do_compile:prepend() {
    CURL_CA_BUNDLE=${STAGING_DIR_NATIVE}/etc/ssl/certs/ca-certificates.crt
    export CURL_CA_BUNDLE

    # Point skia-bindings to use pre-prepared Skia source
    export SKIA_SOURCE_DIR="${UNPACKDIR}/skia-source"
    export SKIA_GN_COMMAND="${STAGING_BINDIR_NATIVE}/gn"
    export SKIA_NINJA_COMMAND="${STAGING_BINDIR_NATIVE}/ninja"
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
