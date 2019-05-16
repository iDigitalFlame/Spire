#NoTrayIcon
#Region
#AutoIt3Wrapper_Change2CUI=y
#EndRegion

if($CMDLine[0] > 0) Then
	$var_a = StringSplit($CMDLine[1], "\")
	if(Not IsArray($var_a) Or $var_a[0] == 0 Or $var_a[0] < 2) Then Exit -1
	$var_b = 0
	if($var_a[0] == 4) Then
		$var_b = $var_a[4] == "true"
	EndIf
	if($var_b) Then
		$var_c = RunAsWait($var_a[1], $var_a[2], $var_a[3],
	Else

	EndIf
EndIf