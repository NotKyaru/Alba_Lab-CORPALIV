# Conecta el emulador (o un teléfono por USB) con los microservicios del PC.
# "adb reverse" hace que 127.0.0.1:808x dentro del dispositivo llegue a localhost:808x del PC.
# Ejecutar cada vez que se inicia el emulador o se conecta el teléfono:
#   powershell -ExecutionPolicy Bypass -File backend\conectar-dispositivo.ps1

$adb = Join-Path $env:LOCALAPPDATA 'Android\Sdk\platform-tools\adb.exe'
if ($env:ANDROID_HOME) { $adb = Join-Path $env:ANDROID_HOME 'platform-tools\adb.exe' }
if (-not (Test-Path $adb)) {
    Write-Error "No se encontró adb en $adb. Instala Android SDK Platform-Tools o define ANDROID_HOME."
    exit 1
}

& $adb wait-for-device
foreach ($puerto in 8081, 8082, 8083) {
    & $adb reverse "tcp:$puerto" "tcp:$puerto" | Out-Null
    if ($LASTEXITCODE -ne 0) { Write-Error "Falló adb reverse en el puerto $puerto"; exit 1 }
}
Write-Output 'Dispositivo conectado a los microservicios:'
& $adb reverse --list
