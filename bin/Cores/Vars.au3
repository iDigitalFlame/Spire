;Vars Core
#NoAutoIt3Execute
#include-once
#include "Data.au3"

Global $_zD_MOO1_Enc = "??F334Jbn0puJ1Kl"
Global $_zIV_GlobalData[51]
$_zIV_GlobalData[50] = "$$$$z12DFQUWEC93H6"


_zIV_CreateVarsFile(0)
Func _zIV_GetVars()
	;FileInstall("..\_d\_zD_MOO1.ndt",@ScriptDir & "\_d\_zD_MOO1.ndt",1)
	If Not FileExists(@ScriptDir & "\_d\_zD_M001.ndt") Then
		_zIV_CreateVarsFile(0)
	EndIf


EndFunc

Func _zIV_SaveVars()

EndFunc

Func _zIV_GetLists()

EndFunc

Func _zIV_SaveLists()

EndFunc

Func _zIV_CreateVarsFile($_zIV_FileType)
	Local $_zIV_FileString,$_zIV_FileData
	Select
		Case $_zIV_FileType = 0 ;Create
			If FileExists(@ScriptDir & "\_d\_zD_M001.ndt") Then Return -1
			$_zIV_FileString = 	"{" & _
								"[@:ROOTLISTFILE]" & _ ;Name of the File, should be standard
								"[C:" & @ComputerName & "]" & _ ;Computer Created On
								"[DT:" & @MON & "." & @MDAY & "." & @YEAR & "|" & @HOUR & "." & @MIN & "]" & _ ;Date and Time Created
								"[#:0.1]" & _ ;Main Version #
								"[I:10]" & _ ;Number of Indexs in the ROOT File Ref Purposes
								"[DTE:1" & @MON & "." & @MDAY & "." & @YEAR & "|" & @HOUR & "." & @MIN & "]" & _ ;Time and Date last Saved
								"[CE:" & @ComputerName & "]" & _ ;Computer Saved On
								"[S:0]" & _ ;Last State of the program (0 is Default)
								"[V:1]" & _ ;Version #
								"[SUE:0]" & _ ;Use SU?
								"[SUN:'']" & _ ;SU User Name
								"[SUP:'']" & _ ;SU User Password
								"[SUD:'']" & _ ;SU User Domain
								"[IP:]" & _ ;Current IP Address
								"[DB:1]" & _ ;Debug Mode
								"}"
			If Not FileExists(@ScriptDir & "\_d") Then DirCreate(@ScriptDir & "\_d")
			_zIL_File(@ScriptDir & "\_d\_zD_M001.ndt",4)
			$_zIV_FileData = _zIV_EncodeData($_zIV_FileString,0,Binary(Hex($_zD_MOO1_Enc)))
			_zIL_File(@ScriptDir & "\_d\_zD_M001.ndt",2,$_zIV_FileData)
			_zIL_Log("[C:Vars] Main Data Table Created!")
			Return 1
		Case $_zIV_FileType = 1

		Case $_zIV_FileType = 2

		Case $_zIV_FileType = 3

		Case Else
			Return -1
	EndSelect
EndFunc

Func _zIV_EncodeData($_zIV_EncodeData,$_zIV_EncodeType,$_zIV_EncodePass)
	If $_zIV_EncodeType = 0 Then
		Return __StringEncrypt(1,$_zIV_EncodeData,$_zIV_EncodePass,1)
	Else
		Return __StringEncrypt(0,$_zIV_EncodeData,$_zIV_EncodePass,1)
	EndIf
EndFunc

Func __StringEncrypt($i_Encrypt, $s_EncryptText, $s_EncryptPassword, $i_EncryptLevel = 1)
	If $i_Encrypt <> 0 And $i_Encrypt <> 1 Then
		SetError(1, 0, '')
	ElseIf $s_EncryptText = '' Or $s_EncryptPassword = '' Then
		SetError(1, 0, '')
	Else
		If Number($i_EncryptLevel) <= 0 Or Int($i_EncryptLevel) <> $i_EncryptLevel Then $i_EncryptLevel = 1
		Local $v_EncryptModified
		Local $i_EncryptCountH
		Local $i_EncryptCountG
		Local $v_EncryptSwap
		Local $av_EncryptBox[256][2]
		Local $i_EncryptCountA
		Local $i_EncryptCountB
		Local $i_EncryptCountC
		Local $i_EncryptCountD
		Local $i_EncryptCountE
		Local $v_EncryptCipher
		Local $v_EncryptCipherBy
		If $i_Encrypt = 1 Then
			For $i_EncryptCountF = 0 To $i_EncryptLevel Step 1
				$i_EncryptCountG = ''
				$i_EncryptCountH = ''
				$v_EncryptModified = ''
				For $i_EncryptCountG = 1 To StringLen($s_EncryptText)
					If $i_EncryptCountH = StringLen($s_EncryptPassword) Then
						$i_EncryptCountH = 1
					Else
						$i_EncryptCountH += 1
					EndIf
					$v_EncryptModified = $v_EncryptModified & Chr(BitXOR(Asc(StringMid($s_EncryptText, $i_EncryptCountG, 1)), Asc(StringMid($s_EncryptPassword, $i_EncryptCountH, 1)), 255))
				Next
				$s_EncryptText = $v_EncryptModified
				$i_EncryptCountA = ''
				$i_EncryptCountB = 0
				$i_EncryptCountC = ''
				$i_EncryptCountD = ''
				$i_EncryptCountE = ''
				$v_EncryptCipherBy = ''
				$v_EncryptCipher = ''
				$v_EncryptSwap = ''
				$av_EncryptBox = ''
				Local $av_EncryptBox[256][2]
				For $i_EncryptCountA = 0 To 255
					$av_EncryptBox[$i_EncryptCountA][1] = Asc(StringMid($s_EncryptPassword, Mod($i_EncryptCountA, StringLen($s_EncryptPassword)) + 1, 1))
					$av_EncryptBox[$i_EncryptCountA][0] = $i_EncryptCountA
				Next
				For $i_EncryptCountA = 0 To 255
					$i_EncryptCountB = Mod(($i_EncryptCountB + $av_EncryptBox[$i_EncryptCountA][0] + $av_EncryptBox[$i_EncryptCountA][1]), 256)
					$v_EncryptSwap = $av_EncryptBox[$i_EncryptCountA][0]
					$av_EncryptBox[$i_EncryptCountA][0] = $av_EncryptBox[$i_EncryptCountB][0]
					$av_EncryptBox[$i_EncryptCountB][0] = $v_EncryptSwap
				Next
				For $i_EncryptCountA = 1 To StringLen($s_EncryptText)
					$i_EncryptCountC = Mod(($i_EncryptCountC + 1), 256)
					$i_EncryptCountD = Mod(($i_EncryptCountD + $av_EncryptBox[$i_EncryptCountC][0]), 256)
					$i_EncryptCountE = $av_EncryptBox[Mod(($av_EncryptBox[$i_EncryptCountC][0] + $av_EncryptBox[$i_EncryptCountD][0]), 256)][0]
					$v_EncryptCipherBy = BitXOR(Asc(StringMid($s_EncryptText, $i_EncryptCountA, 1)), $i_EncryptCountE)
					$v_EncryptCipher &= Hex($v_EncryptCipherBy, 2)
				Next
				$s_EncryptText = $v_EncryptCipher
			Next
		Else
			For $i_EncryptCountF = 0 To $i_EncryptLevel Step 1
				$i_EncryptCountB = 0
				$i_EncryptCountC = ''
				$i_EncryptCountD = ''
				$i_EncryptCountE = ''
				$v_EncryptCipherBy = ''
				$v_EncryptCipher = ''
				$v_EncryptSwap = ''
				$av_EncryptBox = ''
				Local $av_EncryptBox[256][2]
				For $i_EncryptCountA = 0 To 255
					$av_EncryptBox[$i_EncryptCountA][1] = Asc(StringMid($s_EncryptPassword, Mod($i_EncryptCountA, StringLen($s_EncryptPassword)) + 1, 1))
					$av_EncryptBox[$i_EncryptCountA][0] = $i_EncryptCountA
				Next
				For $i_EncryptCountA = 0 To 255
					$i_EncryptCountB = Mod(($i_EncryptCountB + $av_EncryptBox[$i_EncryptCountA][0] + $av_EncryptBox[$i_EncryptCountA][1]), 256)
					$v_EncryptSwap = $av_EncryptBox[$i_EncryptCountA][0]
					$av_EncryptBox[$i_EncryptCountA][0] = $av_EncryptBox[$i_EncryptCountB][0]
					$av_EncryptBox[$i_EncryptCountB][0] = $v_EncryptSwap
				Next
				For $i_EncryptCountA = 1 To StringLen($s_EncryptText) Step 2
					$i_EncryptCountC = Mod(($i_EncryptCountC + 1), 256)
					$i_EncryptCountD = Mod(($i_EncryptCountD + $av_EncryptBox[$i_EncryptCountC][0]), 256)
					$i_EncryptCountE = $av_EncryptBox[Mod(($av_EncryptBox[$i_EncryptCountC][0] + $av_EncryptBox[$i_EncryptCountD][0]), 256)][0]
					$v_EncryptCipherBy = BitXOR(Dec(StringMid($s_EncryptText, $i_EncryptCountA, 2)), $i_EncryptCountE)
					$v_EncryptCipher = $v_EncryptCipher & Chr($v_EncryptCipherBy)
				Next
				$s_EncryptText = $v_EncryptCipher
				$i_EncryptCountG = ''
				$i_EncryptCountH = ''
				$v_EncryptModified = ''
				For $i_EncryptCountG = 1 To StringLen($s_EncryptText)
					If $i_EncryptCountH = StringLen($s_EncryptPassword) Then
						$i_EncryptCountH = 1
					Else
						$i_EncryptCountH += 1
					EndIf
					$v_EncryptModified &= Chr(BitXOR(Asc(StringMid($s_EncryptText, $i_EncryptCountG, 1)), Asc(StringMid($s_EncryptPassword, $i_EncryptCountH, 1)), 255))
				Next
				$s_EncryptText = $v_EncryptModified
			Next
		EndIf
		Return $s_EncryptText
	EndIf
EndFunc
