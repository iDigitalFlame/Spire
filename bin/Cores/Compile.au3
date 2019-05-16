;Compile Core
#NoAutoIt3Execute
#include-once
#include "Data.au3"
#include "Vars.au3"

Global $_zIC_FilesList
Global $_zIC_SavedFile

Func _zIC_PrepCompile($_zIC_Files = "")
	If Not FileExists(@ScriptDir & "\CT") 		Then 		DirCreate(@ScriptDir & "\CT")
	If Not FileExists(@ScriptDir & "\Cores")	Then 		DirCreate(@ScriptDir & "\Cores")
	If Not FileExists(@ScriptDir & "\R") 		Then 		DirCreate(@ScriptDir & "\R")
	FileInstall("..\..\CT\C32.exe", 			@ScriptDir & "\CT\C32.exe",1) 			;32bit Compiler Base
	FileInstall("..\CT\C64.exe",			@ScriptDir & "\CT\C64.exe",1) 			;64bit Compiler Base
	FileInstall("..\CT\upx.exe", 			@ScriptDir & "\CT\upx.exe",1) 			;Base Compiler Resources
	FileInstall("..\CT\AutoItSC_x64.bin", 	@ScriptDir & "\CT\AutoItSC_x64.bin",1)	;64bit Compiler Resources
	FileInstall("..\CT\AutoItSC.bin", 		@ScriptDir & "\CT\AutoItSC.bin",1)		;32bit Compiler Resources
	FileInstall("..\R\Array.au3", 			@ScriptDir & "\R\Array.au3",1)			;Base Include
	FileInstall("..\R\File.au3", 			@ScriptDir & "\R\File.au3",1)			;Base Include
	FileInstall("..\R\FC.au3", 				@ScriptDir & "\R\FC.au3",1)				;Base Include
	FileInstall("Cores\Compile.au3", 		@ScriptDir & "\Cores\Compile.au3",1)	;Compile Core
	FileInstall("Cores\API.au3", 			@ScriptDir & "\Cores\API.au3",1)		;Vars Core
	FileInstall("APIBase.au3", 				@ScriptDir & "\APIBase.au3",1)			;Base
	$_zIC_FilesList = $_zIC_Files
EndFunc
Func _zIC_Compile($_zIC_AU3,$_zIC_ICO = "",$_zIC_IS64 = 0)
	_zIL_Log("[C:Compile] Starting Compile")
	Local $_zIC_ExeName = @ScriptDir & "\" & Chr(Random(Asc("a"),Asc("z"),1)) & ".exe"
	If $_zIC_ICO <> "" Then
		If Not FileExists($_zIC_ICO) Then
			_zIL_Log("[C:Compile] Icon given does not Exist! Canceling Compile!",2)
			Return -2
		Else
			$_zIC_ICO = '/icon "' & $_zIC_ICO & '" '
		EndIf
	EndIf
	Select
		Case $_zIC_IS64 = 2
			$_zIC_IS64 = ""
		Case $_zIC_IS64 = 1
			$_zIC_IS64 = " /x86"
		Case $_zIC_IS64 = 0
			If @OSArch = "X64" Then
				$_zIC_IS64 = ""
			Else
				$_zIC_IS64 = " /x86"
			EndIf
		EndSelect
	If @OSArch = "X64" Then
		If $_zIC_IS64 <> "" Then
			_zIL_Log("[C:Compile] Compiling as 32bit, under 64bit compiler")
		Else
			_zIL_Log("[C:Compile] Compiling as 64bit, under 64bit compiler")
		EndIf
		If Not FileExists(@ScriptDir & '\CT\C64.exe') Then
			_zIL_Log("[C:Compile] Compiler does not Exist! Exiting",2)
			Return -1
		EndIf
		_zIL_Log("[C:Compile] Runnning Compile")
		$_zIC_Proc = RunWait(@ScriptDir & '\CT\C64.exe /in "' & @ScriptDir & "\" & $_zIC_AU3 & '" /out "' & $_zIC_ExeName & '" /nopack ' & $_zIC_ICO & '/comp 2' & $_zIC_IS64,@ScriptDir,@SW_HIDE)
		ProcessWaitClose($_zIC_Proc)
		_zIL_Log("[C:Compile] Compile Complete! File: " & $_zIC_ExeName & " Created!")
		$_zIC_SavedFile = $_zIC_ExeName
		Return $_zIC_ExeName
	Else
		If $_zIC_IS64 <> "" Then
			_zIL_Log("[C:Compile] Compiling as 32bit, under 32bit compiler")
		Else
			_zIL_Log("[C:Compile] Compiling as 64bit, under 32bit compiler")
		EndIf
		If Not FileExists(@ScriptDir & '\CT\C64.exe') Then
			_zIL_Log("[C:Compile] Compiler does not Exist! Exiting",2)
			Return -1
		EndIf
		_zIL_Log("[C:Compile] Runnning Compile")
		$_zIC_Proc = RunWait(@ScriptDir & '\CT\C32.exe /in "' & $_zIC_AU3 & '" /out "' & $_zIC_ExeName & '" /nopack ' & $_zIC_ICO & '/comp 2' & $_zIC_IS64,@ScriptDir,@SW_HIDE)
		ProcessWaitClose($_zIC_Proc)
		_zIL_Log("[C:Compile] Compile Complete! File: " & $_zIC_ExeName & " Created!")
		$_zIC_SavedFile = $_zIC_ExeName
		Return $_zIC_ExeName
	EndIf
	Return -1
EndFunc
Func _zIC_CleanCompile()
	_zIL_Log("[C:Compile] Cleaning up")
	While UBound($_zIC_FilesList)
		$_zICT_File = __zIC_ArrayPop($_zIC_FilesList)
		FileDelete($_zICT_File)
		_zIL_Log("[C:Compile] Deleted: " & $_zICT_File)
	WEnd
	FileDelete(@ScriptDir & "\MainBase.au3")
	DirRemove(@ScriptDir & "\CT",1)
	DirRemove(@ScriptDir & "\R",1)
	DirRemove(@ScriptDir & "\Cores",1)
	_zIL_Log("[C:Compile] Cleanup Complete!")
EndFunc
Func _zIC_FinishCompile($_zIC_Restart = 0)
	If $_zIC_Restart = 1 Then
		_zIL_Log("[C:Compile] Preparing Restart")
		_zIL_File(@ScriptDir & "\r.bat",4)
		_zIL_File(@ScriptDir & "\r.bat",2,'@echo off' & @CRLF & 'ping 127.0.0.1 -n 2' & @CRLF & 'start "" "' & @ScriptFullPath & '"' & @CRLF & 'del /f /q %0')
		Run(@ScriptDir & "\r.bat",@ScriptDir,@SW_HIDE)
	EndIf
	_zIL_Log("[C:Compile] Terminating Application")
	_zIL_File(@ScriptDir & "\m.bat",4)
	_zIL_File(@ScriptDir & "\m.bat",2,'@echo off' & @CRLF & 'ping 127.0.0.1 -n 1' & @CRLF & 'move /y "' & $_zIC_SavedFile & '" "' & @ScriptFullPath & '"' & @CRLF & 'del /f /q %0')
	Run(@ScriptDir & "\m.bat",@ScriptDir,@SW_HIDE)
	;Run(@ComSpec & ' /c move /y "' & $_zIC_SavedFile & '" "' & @ScriptFullPath & '"',@ScriptDir,@SW_HIDE)
	Exit
EndFunc
Func __zIC_ArrayPop(ByRef $avArray)
	If (Not IsArray($avArray)) Then Return SetError(1, 0, "")
	If UBound($avArray, 0) <> 1 Then Return SetError(2, 0, "")
	Local $iUBound = UBound($avArray) - 1, $sLastVal = $avArray[$iUBound]
	If Not $iUBound Then
		$avArray = ""
	Else
		ReDim $avArray[$iUBound]
	EndIf
	Return $sLastVal
EndFunc
Func _zIC_CompileScript($_zIC_Script,$_zIC_Icon,$_zIC_CompMode,$_zIC_RestartMode,$_zIC_FileList = "")
	_zIC_PrepCompile($_zIC_FileList)
	_zIV_SaveVars()
	_zIC_Compile($_zIC_Script,$_zIC_Icon,$_zIC_CompMode)
	_zIC_CleanCompile()
	_zIC_FinishCompile($_zIC_RestartMode)
EndFunc
Func _zIC_CompileCopy($_zIC_Script,$_zIC_Icon,$_zIC_CompMode,$_zIC_ResultFile,$_zIC_FileList = "")
	_zIC_PrepCompile($_zIC_FileList)
	_zIV_SaveVars()
	$_zICT_CompFile = _zIC_Compile($_zIC_Script,$_zIC_Icon,$_zIC_CompMode)
	_zIC_CleanCompile()
	Run(@ComSpec & ' /c move /y "' & $_zICT_CompFile & '" "' & $_zIC_ResultFile & '"',@ScriptDir,@SW_HIDE)
EndFunc