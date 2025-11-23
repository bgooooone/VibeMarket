@echo off
echo ========================================
echo VibeMarket JDK 17 环境配置脚本
echo ========================================
echo.

REM 检查JDK是否存在
if not exist "D:\javajdk\bin\java.exe" (
    echo [错误] 未找到 JDK 17，请确认 JDK 已安装在 D:\javajdk
    echo.
    pause
    exit /b 1
)

echo [信息] 找到 JDK 17 在 D:\javajdk
echo.

REM 设置JAVA_HOME
echo [步骤 1/3] 设置 JAVA_HOME 环境变量...
setx JAVA_HOME "D:\javajdk" /M
if errorlevel 1 (
    echo [警告] 需要管理员权限来设置系统环境变量
    echo 请以管理员身份运行此脚本，或手动设置 JAVA_HOME=D:\javajdk
) else (
    echo [成功] JAVA_HOME 已设置为 D:\javajdk
)
echo.

REM 检查Path中是否已包含JAVA_HOME\bin
echo [步骤 2/3] 检查 PATH 环境变量...
set PATH_CHECK=%PATH%
echo %PATH_CHECK% | findstr /C:"%JAVA_HOME%\bin" >nul
if errorlevel 1 (
    echo [信息] PATH 中未找到 JDK，需要添加
    echo 请手动将 %JAVA_HOME%\bin 添加到系统 PATH 环境变量
    echo 或运行以下命令（需要管理员权限）：
    echo setx PATH "%PATH%;%JAVA_HOME%\bin" /M
) else (
    echo [成功] PATH 中已包含 JDK
)
echo.

REM 验证Java版本
echo [步骤 3/3] 验证 Java 版本...
"D:\javajdk\bin\java.exe" -version
if errorlevel 1 (
    echo [错误] Java 验证失败
    pause
    exit /b 1
)
echo.

echo ========================================
echo 配置完成！
echo ========================================
echo.
echo 注意：环境变量更改需要重新打开命令行窗口才能生效
echo 请关闭当前窗口并重新打开，然后运行以下命令验证：
echo   java -version
echo   echo %JAVA_HOME%
echo.
pause

