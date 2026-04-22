TARGET_CFLAGS:remove = "-fcanon-prefix-map"

do_compile:prepend() {
    #export RUSTFLAGS="${RUSTFLAGS}"
    #export RUST_TARGET_PATH="${RUST_TARGET_PATH}"
    # Make sure that Skia's invocation of clang to generate bindings.rs for the Skia headers
    # passes the right flags, in particular float abi selection
    export BINDGEN_EXTRA_CLANG_ARGS="${HOST_CC_ARCH} ${TOOLCHAIN_OPTIONS} ${TARGET_CFLAGS}"
}

# Emulate what clang-environment.inc does.

export TARGET_CLANGCC_ARCH = "${TARGET_CC_ARCH}"
TARGET_CLANGCC_ARCH:remove = "-mthumb-interwork"
TARGET_CLANGCC_ARCH:remove = "-mmusl"
TARGET_CLANGCC_ARCH:remove = "-muclibc"
TARGET_CLANGCC_ARCH:remove = "-meb"
TARGET_CLANGCC_ARCH:remove = "-mel"
TARGET_CLANGCC_ARCH:append = "${@bb.utils.contains("TUNE_FEATURES", "bigendian", " -mbig-endian", " -mlittle-endian", d)}"
TARGET_CLANGCC_ARCH:remove:powerpc = "-mhard-float"
TARGET_CLANGCC_ARCH:remove:powerpc = "-mno-spe"
TARGET_CLANGCC_ARCH:remove = "-fcanon-prefix-map"

# Add -I=/usr/include/freetype2 as skia has hardcoded it to -I/usr/include/freetype2, which
# would locate freetype in the host system, not the sysroot target.
export CLANGCC="${TARGET_PREFIX}clang --target=${TARGET_SYS} ${TARGET_CLANGCC_ARCH} --sysroot=${STAGING_DIR_TARGET}  -I=/usr/include/freetype2 -ffile-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${PV}"
export CLANGCXX="${TARGET_PREFIX}clang++ --target=${TARGET_SYS} ${TARGET_CLANGCC_ARCH} --sysroot=${STAGING_DIR_TARGET}  -I=/usr/include/freetype2 -ffile-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${PV}"
export CLANGCPP="${TARGET_PREFIX}clang -E --target=${TARGET_SYS} ${TARGET_CLANGCC_ARCH} --sysroot=${STAGING_DIR_TARGET}  -I=/usr/include/freetype2 -ffile-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${PV}"
export CLANG_TIDY_EXE="${TARGET_PREFIX}clang-tidy"
export SDKTARGETSYSROOT="${PKG_CONFIG_SYSROOT_DIR}"

# Forward proxy settings from the BitBake variable datastore into every task
# shell environment for recipes that inherit this class. No URLs are hardcoded
# here; values originate from the caller's environment (e.g. a Docker ENV
# directive, local.conf, or shell exports captured via BB_ENV_PASSTHROUGH_ADDITIONS
# in conf/setenv). This ensures proxy settings reach curl sub-processes spawned
# deep inside Cargo build scripts (e.g. skia-bindings downloading Skia sources
# or pre-built binaries) without requiring any site-specific configuration in
# the recipe itself.
export http_proxy
export https_proxy
export HTTP_PROXY
export HTTPS_PROXY
export no_proxy
export NO_PROXY
