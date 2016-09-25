
rem this version requests GB instead of MB, since it will be a
rem bit easier to work with. youre welcome, everyone.

rem the more i get to know the power of batch commands...
rem ...the better the programs become.

rem this comes with commentary for each section for those who
rem want to get into this stuff too...

rem btw the 'rem' at the beginning is nessecary for CMD to skip
rem these certain lines without trying to execute them

rem have fun
rem  ~mineLOLpride

rem checking for the existence of the midi player in the
rem same directory as the launcher

:check
@echo off
cls
echo.
echo Searching for 'TGMSynthesizerTester.jar' in
echo %cd%
echo.
if exist "%cd%\TGMSynthesizerTester.jar" goto main
goto check_fail

rem jump to the main area if it succeeds
rem jump to fail if it doesnt find it

:check_fail
cls
echo.
echo                                                   ,---. 
echo  ,------. ,------.  ,------.   ,-----.  ,------.  I   I 
echo  I  .---' I  .--. ' I  .--. ' '  .-.  ' I  .--. ' I  .' 
echo  I  `--,  I  '--'.' I  '--'.' I  I I  I I  '--'.' I  I  
echo  I  `---. I  I\  \  I  I\  \  '  '-'  ' I  I\  \  `--'  
echo  `------' `--' '--' `--' '--'  `-----'  `--' '--' .--.  
echo                                                   '--'  
echo   'TGMSynthesizerTester.jar' is missing from current directory
echo   '%cd%'
echo.
echo     [1] - Retry       [2] - Exit
echo.

choice /c 12 /n
if errorlevel 2 goto exit
if errorlevel 1 goto check





:main
cls
echo TTTTTTTTTTTTTTTTTTTTTTT       GGGGGGGGGGGGGMMMMMMMM               MMMMMMMM
echo T:::::::::::::::::::::T    GGG::::::::::::GM:::::::M             M:::::::M
echo T:::::::::::::::::::::T  GG:::::::::::::::GM::::::::M           M::::::::M
echo T:::::TT:::::::TT:::::T G:::::GGGGGGGG::::GM:::::::::M         M:::::::::M
echo TTTTTT  T:::::T  TTTTTTG:::::G       GGGGGGM::::::::::M       M::::::::::M
echo         T:::::T       G:::::G              M:::::::::::M     M:::::::::::M
echo         T:::::T       G:::::G              M:::::::M::::M   M::::M:::::::M
echo         T:::::T       G:::::G    GGGGGGGGGGM::::::M M::::M M::::M M::::::M
echo         T:::::T       G:::::G    G::::::::GM::::::M  M::::M::::M  M::::::M
echo         T:::::T       G:::::G    GGGGG::::GM::::::M   M:::::::M   M::::::M
echo         T:::::T       G:::::G        G::::GM::::::M    M:::::M    M::::::M
echo         T:::::T        G:::::G       G::::GM::::::M     MMMMM     M::::::M
echo       TT:::::::TT       G:::::GGGGGGGG::::GM::::::M               M::::::M
echo       T:::::::::T        GG:::::::::::::::GM::::::M               M::::::M
echo       T:::::::::T          GGG::::::GGG:::GM::::::M               M::::::M
echo       TTTTTTTTTTT             GGGGGG   GGGGMMMMMMMM               MMMMMMMM
echo.
echo   MIDI SYNTH TESTER BY TheGhastModding     //     LAUNCHER BY mineLOLpride
echo.
echo              type START to begin                type LEAVE to exit
echo.

rem this part is a little complicated to explain, as i learned
rem how to do this very recently

rem this essentially asks for a string of text to be entered to continue

set /p _begin=:: || set _begin=undefined
if "%_begin%"=="undefined" goto main
if /i "%_begin%"=="start" goto ram_select
if /i "%_begin%"=="leave" goto ending
goto main



:ram_select

rem this is where we will use user inpute to determine how much
rem RAM to start the program with.

rem the input will be stored in a variable temporarily as long
rem as the CMD window remains open.

cls.
echo   ___    __    _          __   ____  _     ____  __  _____  _   ___   _    
echo  I I_)  / /\  I I\/I     ( (  I I_  I I   I I_  / /\  I I  I I / / \ I I\ I
echo  I_I \ /_/--\ I_I  I      )_) I_I__ I_I__ I_I__ \_\/  I_I  I_I \_\_/ I_I \I
echo.
echo              Please type your desired amount of RAM in GB
echo.
echo                          MB / 1024 = GB
echo.

rem once again, this is where user string input comes in handy
rem why did i not know of this earlier

set /p _ram=:: || set _ram=undefined
if /i "%_ram%" == "undefined" goto ram_error_undefined
set ram=%_ram%
goto program_launch

rem this is where the result is decided depending on the user input

:ram_error_undefined

rem if the user didnt input anything, this is where the
rem program will point the user to

cls
echo.
echo                                                   ,---. 
echo  ,------. ,------.  ,------.   ,-----.  ,------.  I   I 
echo  I  .---' I  .--. ' I  .--. ' '  .-.  ' I  .--. ' I  .' 
echo  I  `--,  I  '--'.' I  '--'.' I  I I  I I  '--'.' I  I  
echo  I  `---. I  I\  \  I  I\  \  '  '-'  ' I  I\  \  `--'  
echo  `------' `--' '--' `--' '--'  `-----'  `--' '--' .--.  
echo                                                   '--'
echo   The amount of RAM is undefined!
echo.
echo     [1] - Retry       [2] - Exit
echo.

rem this is where using a single keypress is more reliable than
rem an entire string input.

choice /c 12 /n
if errorlevel 2 goto exit
if errorlevel 1 goto ram_select

rem current attempts to create a trigger for low memory have failed
rem i am currently looking for a way to get around it triggering
rem at all times

:ram_error_lowmemory

rem IN THIS VERSION, IT ASKS FOR WHOLE GB, SO USING A LOW MEMORY
rem CHECK IS BASICALLY IMPOSSIBLE! STAY TUNED FOR A VERSION WHERE
rem YOU CAN CHOOSE TO INPUT MB OR GB. THX  ~mineLOLpride

rem if the user input anything below 128, the program will
rem point the user here

cls
echo.
echo                                                   ,---. 
echo  ,------. ,------.  ,------.   ,-----.  ,------.  I   I 
echo  I  .---' I  .--. ' I  .--. ' '  .-.  ' I  .--. ' I  .' 
echo  I  `--,  I  '--'.' I  '--'.' I  I I  I I  '--'.' I  I  
echo  I  `---. I  I\  \  I  I\  \  '  '-'  ' I  I\  \  `--'  
echo  `------' `--' '--' `--' '--'  `-----'  `--' '--' .--.  
echo                                                   '--'
echo   The amount of RAM specified is less than 128 MB!
echo           Also how did you get here?
echo           This check is unused now!
echo.
echo     [1] - Retry       [2] - Exit
echo.

rem more single button inputs

choice /c 12 /n
if errorlevel 2 goto exit
if errorlevel 1 goto ram_select


:program_launch

rem here is the startup sequence for the program

cls
echo.
echo Starting program with
echo %ram% GB of RAM ...
echo.

java -jar -Xmx%ram%G TGMSynthesizerTester.jar -lookAndFeelTest

echo.
echo END OF OUTPUT
pause



:ending

rem this is the part where either the program exits or the program
rem shuts down.

cls

exit
