$ErrorActionPreference = 'Stop'
$previousJavaHome = $env:JAVA_HOME
$previousJavaOptions = $env:JAVA_TOOL_OPTIONS
$previousGradleHome = $env:GRADLE_USER_HOME
try {
    $env:JAVA_HOME = Join-Path $env:USERPROFILE '.jdks\ms-17.0.20.1'
    if (!(Test-Path "$env:JAVA_HOME\bin\java.exe")) { throw 'No se encontro Java 17. Ajustar JAVA_HOME en este script.' }
    $env:JAVA_TOOL_OPTIONS = "$previousJavaOptions -Djdk.net.unixdomain.tmpdir=C:\Windows\Temp".Trim()
    $env:GRADLE_USER_HOME = 'C:\Users\Public\RA_GradleCache'
    Push-Location 'C:\Users\Public\RA_AndroidProject'
    try {
        & .\gradlew.bat -p 'C:\Users\Public\RA_AndroidProject' :app:assembleDebug :app:testDebugUnitTest --console=plain
        $buildExitCode = $LASTEXITCODE
    } finally { Pop-Location }
} finally {
    $env:GRADLE_USER_HOME = $previousGradleHome
    $env:JAVA_HOME = $previousJavaHome
    $env:JAVA_TOOL_OPTIONS = $previousJavaOptions
}
exit $buildExitCode

