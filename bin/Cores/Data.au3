;Data Core
#include-once
#NoAutoIt3Execute

Func _zIL_Log($_zIL_LogData, $_zIL_LogType = 0)
	Local $_zIL_Log
	If Not FileExists(@ScriptDir & "\Logs") Then DirCreate(@ScriptDir & "\Logs")
	if Not FileExists(@ScriptDir & "\Logs\" & @MON & @MDAY & @YEAR & ".txt") Then _zIL_File(@ScriptDir & "\Logs\" & @MON & @MDAY & @YEAR & ".txt",4)
	Select
		Case $_zIL_LogType = 0
			$_zIL_Log = "(" & @MON & "/" & @MDAY & " " & @HOUR & ":" & @MIN & ") " & $_zIL_LogData
		Case $_zIL_LogType = 1
			$_zIL_Log = "(" & @MON & "/" & @MDAY & " " & @HOUR & ":" & @MIN & ") [ERROR] " & $_zIL_LogData
		Case $_zIL_LogType = 2
			$_zIL_Log = "(" & @MON & "/" & @MDAY & " " & @HOUR & ":" & @MIN & ") [CRITICAL] " & $_zIL_LogData
		Case $_zIL_LogType = 3
			$_zIL_Log = "(" & @MON & "/" & @MDAY & " " & @HOUR & ":" & @MIN & ") [SEVERE] " & $_zIL_LogData
		Case $_zIL_LogType = 4
			$_zIL_Log = "(" & @MON & "/" & @MDAY & " " & @HOUR & ":" & @MIN & ") [ROOT] " & $_zIL_LogData
		Case $_zIL_LogType = 5
			$_zIL_Log = "(" & @MON & "/" & @MDAY & " " & @HOUR & ":" & @MIN & ") [CODE] " & $_zIL_LogData
		Case $_zIL_LogType = 6
			$_zIL_Log = "(" & @MON & "/" & @MDAY & " " & @HOUR & ":" & @MIN & ") [NET] " & $_zIL_LogData
	EndSelect
	_zIL_File(@ScriptDir & "\Logs\" & @MON & @MDAY & @YEAR & ".txt",1,$_zIL_Log & @CRLF)
EndFunc
Func _zIL_File($_zIL_File, $_zIL_FileMode = 0, $_zIL_Edit = "")
	If $_zIL_File = "" Then Return -1
	Select
		Case $_zIL_FileMode = 0
			$_zILT_Hand0 = FileOpen($_zIL_File, 0)
			if $_zILT_Hand0 = -1 Then Return -1
			$_zILT_String0 = FileRead($_zILT_Hand0)
			FileClose($_zILT_Hand0)
			Return $_zILT_String0
		Case $_zIL_FileMode = 1
			$_zILT_Hand0 = FileOpen($_zIL_File, 1)
			if $_zILT_Hand0 = -1 Then Return -1
			FileWrite($_zILT_Hand0, $_zIL_Edit)
			FileClose($_zILT_Hand0)
			Return 1
		Case $_zIL_FileMode = 2
			$_zILT_Hand0 = FileOpen($_zIL_File, 2)
			if $_zILT_Hand0 = -1 Then Return -1
			FileWrite($_zILT_Hand0, $_zIL_Edit)
			FileClose($_zILT_Hand0)
			Return 1
		Case $_zIL_FileMode = 4
			Local $_zILT_Hand0 = FileOpen($_zIL_File, 2)
			If $_zILT_Hand0 = -1 Then Return SetError(1, 0, 0 )
			Local $_zILT_Hand1 = FileWrite($_zILT_Hand0, "")
			FileClose($_zILT_Hand0)
			If $_zILT_Hand1 = -1 Then Return SetError(2, 0, 0 )
			Return 1
		Case Else
			Return -1
	EndSelect
EndFunc