;Display Core
#NoAutoIt3Execute
#include-once

Global $_zID_BaseIDs[50][50]
Global $_zID_BaseFrame[6]
	$_zID_BaseFrame[0] = -1
	$_zID_BaseFrame[1] = 0
	$_zID_BaseFrame[2] = "blue"
	$_zID_BaseFrame[3] = @DesktopWidth-260
	$_zID_BaseFrame[4] = @DesktopHeight-300
	$_zID_BaseFrame[5] = -1
Global $_zIT_TempData[11]
	$_zIT_TempData[0] = 0
	$_zIT_TempData[1] = 0

Func _zID_SpawnFrame($_zID_BaseState = 1,$_zID_BaseColor = "blue",$_zID_BaseWPos = -1,$_zID_BaseHPos = -1)
	Local $_zIT_SpcX = 0,$_zIT_SpcY = 0,$_zIT_Edit = 0
	$_zIT_TempData[0] = 0
	$_zIT_TempData[1] = 0
	$_zIT_TempData[8] = 0
	$_zIT_TempData[2] = 0
	$_zIT_TempData[10] = 10
	$_zIT_TempData[9] = 10
	If $_zID_BaseFrame[0] <> -1 Then $_zIT_Edit = 1
	If $_zID_BaseWPos = -1 Then $_zID_BaseWPos = @DesktopWidth-260
	If $_zID_BaseHPos = -1 Then $_zID_BaseHPos = @DesktopHeight-300
	$_zID_BaseFrame[1] = $_zID_BaseState
	$_zID_BaseFrame[2] = $_zID_BaseColor
	$_zID_BaseFrame[3] = $_zID_BaseWPos
	$_zID_BaseFrame[4] = $_zID_BaseHPos
	$_zID_BaseFrame[0] = GUICreate("",250,250,$_zID_BaseWPos,$_zID_BaseHPos,0x80000000,BitOR(0x00000008,0x00000080))
	For $_zIT_Int0 = 0 To 49 Step 1
		For $_zIT_Int1 = 0 To 49 Step 1
			_zID_Pos($_zIT_SpcX,$_zIT_SpcY,$_zID_BaseState,$_zIT_Int1,$_zIT_Int0,$_zID_BaseColor,$_zIT_Edit)
			$_zIT_SpcX += 5
		Next
		$_zIT_SpcX = 0
		$_zIT_SpcY += 5
	Next
	If $_zIT_Edit = 0 Then _zID_GUISetInv($_zID_BaseFrame[0])
	If $_zIT_Edit = 0 Then GUISetState()
EndFunc
Func _zID_Pos($_zID_X,$_zID_Y,$_zID_BaseState,$_zIT_Int1,$_zIT_Int0,$_zID_BaseColor,$_zIT_Edit)
	Select
		Case $_zID_BaseState = 1 And $_zID_X >= 50 And $_zID_X <= 200 And $_zID_Y >= 50 And $_zID_Y <= 200 And $_zIT_Edit <> 1
			$_zID_BaseIDs[$_zIT_Int1][$_zIT_Int0] = GUICtrlCreateLabel("",$_zID_X,$_zID_Y,5,5)
			Select
				Case $_zID_Y = 50
					GUICtrlSetBkColor(-1,0x00000000)
				Case $_zID_Y = 200
					GUICtrlSetBkColor(-1,0x00000000)
				Case $_zID_X = 50
					GUICtrlSetBkColor(-1,0x00000000)
				Case $_zID_X = 200
					GUICtrlSetBkColor(-1,0x00000000)
				Case Else
					GUICtrlSetBkColor(-1,_zID_GetRandomColor($_zID_BaseColor))
			EndSelect
		Case $_zID_BaseState = 1 And $_zID_X >= 50 And $_zID_X <= 200 And $_zID_Y >= 50 And $_zID_Y <= 200 And $_zIT_Edit = 1
			If GUICtrlGetState($_zID_BaseIDs[$_zIT_Int1][$_zIT_Int0]) = -1 Then $_zID_BaseIDs[$_zIT_Int1][$_zIT_Int0] = GUICtrlCreateLabel("",$_zID_X,$_zID_Y,5,5)
			Select
				Case $_zID_Y >= 50 And $_zID_X >= 50 And $_zID_Y <= 65 And $_zID_X <= 65 And $_zIT_TempData[10] >= 1
					If $_zIT_TempData[10] = 1 And $_zIT_TempData[7] <> $_zID_Y Then
						$_zIT_TempData[10] = 10-$_zIT_TempData[8]
					EndIf
					If $_zIT_TempData[7] <> $_zID_Y Then $_zIT_TempData[7] = $_zID_Y
					GUICtrlDelete($_zID_BaseIDs[$_zIT_Int1][$_zIT_Int0])
					$_zIT_TempData[10] = $_zIT_TempData[10]-1
					$_zIT_TempData[8] = $_zIT_TempData[8]+1
				Case $_zID_Y = 50
					GUICtrlSetBkColor($_zID_BaseIDs[$_zIT_Int1][$_zIT_Int0],0x00000000)
				Case $_zID_Y = 200
					GUICtrlSetBkColor($_zID_BaseIDs[$_zIT_Int1][$_zIT_Int0],0x00000000)
				Case $_zID_X = 50
					GUICtrlSetBkColor($_zID_BaseIDs[$_zIT_Int1][$_zIT_Int0],0x00000000)
				Case $_zID_X = 200
					GUICtrlSetBkColor($_zID_BaseIDs[$_zIT_Int1][$_zIT_Int0],0x00000000)
				Case Else
					GUICtrlSetBkColor($_zID_BaseIDs[$_zIT_Int1][$_zIT_Int0],_zID_GetRandomColor($_zID_BaseColor))
			EndSelect
	EndSelect
EndFunc

Func _zID_GetRandomColor($_zID_ColorName = "blue")
	Select
		Case StringCompare($_zID_ColorName,"blue",0) = 0
			Return "0x" & Hex(BitAND(Random(0,100,1),255),2) & Hex(BitAND(Random(0,255,1),255),2) & Hex(BitAND(Random(150,255,1),255),2)
		Case StringCompare($_zID_ColorName,"red",0) = 0
			Return "0x" & Hex(BitAND(Random(150,255,1),255),2) & Hex(BitAND(Random(0,100,1),255),2) & Hex(BitAND(Random(0,100,1),255),2)
		Case StringCompare($_zID_ColorName,"green",0) = 0
			Return "0x" & Hex(BitAND(Random(0,100,1),255),2) & Hex(BitAND(Random(0,255,1),255),2) & Hex(BitAND(Random(0,100,1),255),2)
		Case StringCompare($_zID_ColorName,"purple",0) = 0
			Return "0x" & Hex(BitAND(Random(0,150,1),255),2) & Hex(BitAND(Random(0,100,1),255),2) & Hex(BitAND(Random(150,255,1),255),2)
		Case StringCompare($_zID_ColorName,"yellow",0) = 0
			Return "0x" & Hex(BitAND(Random(150,255,1),255),2) & Hex(BitAND(Random(150,255,1),255),2) & Hex(BitAND(Random(0,100,1),255),2)
		Case StringCompare($_zID_ColorName,"pink",0) = 0
			Return "0x" & Hex(BitAND(Random(0,255,1),255),2) & Hex(BitAND(Random(0,100,1),255),2) & Hex(BitAND(Random(150,200,1),255),2)
		Case StringCompare($_zID_ColorName,"orange",0) = 0
			Return "0x" & Hex(BitAND(Random(200,255,1),255),2) & Hex(BitAND(Random(50,200,1),255),2) & Hex(BitAND(Random(0,100,1),255),2)
		Case StringCompare($_zID_ColorName,"random",0) = 0
			Return "0x" & Hex(BitAND(Random(0,255,1),255),2) & Hex(BitAND(Random(0,255,1),255),2) & Hex(BitAND(Random(0,255,1),255),2)
		Case Else
			Return "0x" & Hex(BitAND(Random(0,100,1),255),2) & Hex(BitAND(Random(0,255,1),255),2) & Hex(BitAND(Random(150,255,1),255),2)
	EndSelect
EndFunc
Func _zID_SpawnBox($_zID_BaseColor = "0xF2DC82",$_zID_Clear = 0)
	Local $_zIT_SpcX = 0,$_zIT_SpcY = 0
	If $_zID_BaseFrame[1] = 0 Then
		$_zID_BaseFrame[0] = GUICreate("",250,250,$_zID_BaseFrame[3],$_zID_BaseFrame[4],0x80000000,BitOR(0x00000008,0x00000080))
	EndIf
	For $_zIT_Int0 = 0 To 8 Step 1
		For $_zIT_Int1 = 0 To 35 Step 1
			$_zID_BaseIDs[$_zIT_Int1][$_zIT_Int0] = GUICtrlCreateLabel("",$_zIT_SpcX,$_zIT_SpcY,5,5)
			GUICtrlSetBkColor(-1,0xF2DC82)
			If $_zIT_Int0 = 0 Then GUICtrlSetBkColor(-1,0x00000000)
			If $_zIT_Int0 = 8 Then GUICtrlSetBkColor(-1,0x00000000)
			If $_zIT_Int1 = 0 Then GUICtrlSetBkColor(-1,0x00000000)
			If $_zIT_Int1 = 35 Then GUICtrlSetBkColor(-1,0x00000000)
			If $_zIT_Int0 = 0 And $_zIT_Int1 = 0 Then GUICtrlDelete(-1)
			If $_zIT_Int0 = 0 And $_zIT_Int1 = 35 Then GUICtrlDelete(-1)
			If $_zIT_Int0 = 8 And $_zIT_Int1 = 0 Then GUICtrlDelete(-1)
			If $_zIT_Int0 = 8 And $_zIT_Int1 = 35 Then GUICtrlDelete(-1)
			$_zIT_SpcX += 5
		Next
		$_zIT_SpcX = 0
		$_zIT_SpcY += 5
	Next
	$_zID_BaseFrame[5] = GUICtrlCreateLabel("",5,5,150,20)
	GUICtrlSetBkColor(-1,0xF2DC82)
	_zID_GUISetInv($_zID_BaseFrame[0])
	GUISetState()
EndFunc
Func _zID_GUISetInv($hWnd)
    Local $aClassList, $aM_Mask, $aCtrlPos, $aMask

    $aClassList = StringSplit(_zID_GetClassList($hWnd), @LF)
    $aM_Mask = DllCall("gdi32.dll", "long", "CreateRectRgn", "long", 0, "long", 0, "long", 0, "long", 0)

    For $i = 1 To UBound($aClassList) - 1
        $aCtrlPos = ControlGetPos($hWnd, '', $aClassList[$i])
        If Not IsArray($aCtrlPos) Then ContinueLoop

        $aMask = DllCall("gdi32.dll", "long", "CreateRectRgn", _
            "long", $aCtrlPos[0], _
            "long", $aCtrlPos[1], _
            "long", $aCtrlPos[0] + $aCtrlPos[2], _
            "long", $aCtrlPos[1] + $aCtrlPos[3])
        DllCall("gdi32.dll", "long", "CombineRgn", "long", $aM_Mask[0], "long", $aMask[0], "long", $aM_Mask[0], "int", 2)
    Next
    DllCall("user32.dll", "long", "SetWindowRgn", "hwnd", $hWnd, "long", $aM_Mask[0], "int", 1)
EndFunc
Func _zID_GetClassList($sTitle)
    Local $sClassList = WinGetClassList($sTitle)
    Local $aClassList = StringSplit($sClassList, @LF)
    Local $sRetClassList = "", $sHold_List = "|"
    Local $aiInHold, $iInHold

    For $i = 1 To UBound($aClassList) - 1
        If $aClassList[$i] = "" Then ContinueLoop

        If StringRegExp($sHold_List, "\|" & $aClassList[$i] & "~(\d+)\|") Then
            $aiInHold = StringRegExp($sHold_List, ".*\|" & $aClassList[$i] & "~(\d+)\|.*", 1)
            $iInHold = Number($aiInHold[UBound($aiInHold)-1])

            If $iInHold = 0 Then $iInHold += 1

            $aClassList[$i] &= "~" & $iInHold + 1
            $sHold_List &= $aClassList[$i] & "|"

            $sRetClassList &= $aClassList[$i] & @LF
        Else
            $aClassList[$i] &= "~1"
            $sHold_List &= $aClassList[$i] & "|"
            $sRetClassList &= $aClassList[$i] & @LF
        EndIf
    Next

    Return StringReplace(StringStripWS($sRetClassList, 3), "~", "")
EndFunc