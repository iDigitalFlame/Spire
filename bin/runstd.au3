#NoTrayIcon
#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_Change2CUI=y
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****
#include <Constants.au3>

$var_a = 0
$var_b = ""
$var_c = 0

while($var_a < 10)
	$var_b = ConsoleRead(true)
	$var_c = StringInStr($var_b, "*", 0, -1)
	if($var_c > 0) Then
		doLogin()
		Exit 0
	EndIf
	if(@extended == 0) Then $var_a += 1
	Sleep(10)
WEnd
Exit -1

Func doLogin()
	$varl_a = StringLeft($var_b, $var_c - 1)
	$varl_b = StringSplit($varl_a, "]")
	ConsoleWrite($varl_b[0])
	if($varl_b[0] < 3) Then Exit -1
	if($varl_b[0] > 3) Then
		ConsoleWrite("gt4")
		$varl_d = RunAsWait($varl_b[2], $varl_b[4], $varl_b[3], 2, $varl_b[1], @ScriptDir, @SW_HIDE, $STDERR_CHILD + $STDOUT_CHILD)
		ProcessWaitClose($varl_d)
	Else
		ConsoleWrite("lt4")
		$varl_d = RunAsWait($varl_b[2], @ComputerName, $varl_b[3], 2, $varl_b[1], @ScriptDir, @SW_HIDE, $STDERR_CHILD + $STDOUT_CHILD)
		ProcessWaitClose($varl_d)
	EndIf
EndFunc