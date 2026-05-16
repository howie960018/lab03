@echo off

set "OUTPUT_FILE=project_summary.txt"



echo [treeing...]

tree /f /a > "%OUTPUT_FILE%"



echo [merging...]

for /r %%i in (*) do (

    :: 過濾掉不需要的資料夾與特定副檔名

    echo %%i | findstr /v /i "node_modules .git .jpg .png .exe" >nul && (

        if not "%%~nxi"=="%OUTPUT_FILE%" if not "%%~nxi"=="%~nx0" (

            echo ------------------------------------------ >> "%OUTPUT_FILE%"

            echo FILE: %%i >> "%OUTPUT_FILE%"

            echo ------------------------------------------ >> "%OUTPUT_FILE%"

            type "%%i" >> "%OUTPUT_FILE%"

            echo. >> "%OUTPUT_FILE%"

        )

    )

)

echo done！

pause