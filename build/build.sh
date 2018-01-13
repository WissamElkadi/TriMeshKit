# @Date:   2018-01-13T12:55:51+00:00
# @Last modified time: 2018-01-13T22:58:46+00:00



RED='\033[31m'
GREEN='\033[32m'
WHITE='\033[37m'
CYAN='\033[36m'
PURPLE='\033[35m'

taregtOS=' '
generator=' '
buildCommand=' '
targetArch='all'
buildType='all'
outputDirectory="trimeshkit_build"

declare -a targetArchArray=()
declare -a buildTypeArray=()

usage="${PURPLE} $(basename "$0") [-h] [-t a b o] -- program to build and install TriMeshKit:${WHITE}
    - h  \033[36m show this help text ${WHITE}
    - t  ${RED} [Mandatory] ${CYAN}traget os 'android' or 'windows' ${WHITE}
    - a  ${GREEN} [Optional] ${CYAN} target arch 'armeabi-v7a' 'arm64-v8a' 'win64' 'all' default {all} ${WHITE}
    - b  ${GREEN} [Optional] ${CYAN} build type 'all' 'debug' or 'release' default {all} ${WHITE}
    - o  ${GREEN} [Optional] ${CYAN} output build directory default {trimeshkit_build} ${WHITE}"

while getopts ':ht:a:b:o:' option; do
    case "${option}" in
    h) echo -e "$usage"
        exit
        ;;
    t)
        if [ $OPTARG != 'android' ] && [ $OPTARG != 'windows' ];then
            echo -e "unsupported os";
            echo -e "t values {android, windows}";
            echo -e "$usage";
            exit 0
        fi

        taregtOS=$OPTARG
        ;;
    a)
        if [ $taregtOS = 'android' ]; then
            if [ $OPTARG != 'all' ] && [ $OPTARG != 'armeabi-v7a' ] && [ $OPTARG != 'arm64-v8a' ]; then
                echo -e "unsupported android arch";
                echo -e "a values for android {all, armeabi-v7a, arm64-v8a}";
                echo -e "$usage";
                exit 0
            fi
        else
            if [ $taregtOS = 'windows' ]; then
                if [ $OPTARG != "all" ] && [ $OPTARG != "win64" ]; then
                    echo -e "unsupported windwos arch";
                    echo -e "a values for windows {all, win64}";
                    echo -e "$usage";
                    exit 0
                fi
            fi
        fi
        targetArch=($OPTARG)
         ;;
    b)
        if [ $OPTARG != "all" ] && [ $OPTARG != "debug" ] && [ $OPTARG != "release" ]; then
            echo -e "unsupported build type";
            echo -e "b values {all, debug, release}";
            echo -e "$usage";
            exit 0
        fi
        buildType=($OPTARG)
        ;;
    o)
        outputDirectory=$OPTARG
        ;;
 esac
done

if [[ $taregtOS = ' ' ]]; then
    echo -e "you must provide the mandatory options";
    echo -e "$usage";
    exit 0
else
    if [ $buildType = 'all' ]; then
        buildTypeArray=('debug' 'release')
    else
        buildTypeArray=($buildType)
    fi

    if [ $taregtOS = 'android' ]; then
        if [ $targetArch = 'all' ]; then
            targetArchArray=('armeabi-v7a' 'arm64-v8a')
        else
            targetArchArray=($targetArch)
        fi
        generator='Ninja'
        buildCommand='ninja -j8'
    else
        if [ $taregtOS = 'windows' ]; then
            if [ $targetArch = 'all' ]; then
                targetArchArray=('Win64')
            else
                targetArchArray=($targetArch)
            fi
            generator="Visual Studio 14 2015 Win64"
            buildCommand="MSBuild.exe TriMeshKit.sln"
        fi
    fi
fi

cd ../..

echo -e "\033[36m... build begin ... \033[37m"

mkdir -p $outputDirectory
cd $outputDirectory

mkdir -p $taregtOS
cd $taregtOS


for bt in "${buildTypeArray[@]}" ; do
    mkdir -p $bt
    cd $bt

    for ta in "${targetArchArray[@]}" ; do
        mkdir -p $ta
        cd $ta

        cmake -G "$generator" -DCMAKE_BUILD_TYPE=$bt -DCMAKE_TOOLCHAIN_FILE="../../../../TriMeshKit/build/platforms/$taregtOS/$taregtOS.toolchain.$ta.cmake" ../../../../TriMeshKit/build
        $buildCommand

        cd ../
    done

    cd ../

done

echo -e "\033[36m... build end ... \033[37m"
