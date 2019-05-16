#include-once

Global $input_Stream

Func _readInput()
	Local $read_a, $read_b
	While 1
		$read_a = ConsoleRead()
		$read_b = StringInStr($read_a, "$", 0, -1)
		if($read_b > 0) Then ExitLoop
		Sleep(25)
	WEnd
	$input_Stream = StringLeft($read_a, $read_b - 1)
EndFunc