program exemplo;  // cPalRes
var
  xpt421z: integer;  // cId, cInt
  valor: integer;  // cId, cInt
begin
  valor := 127;  // cAtrib, cInt
  xpt421z := 42;  // cAtrib, cInt
  writeln('Teste de string');  // cString
  For i := 1 to 10 do  // cPalRes, cId, cInt
  begin
    valor := valor + 1;  // cAtrib, cInt, cAdicao
    valor := valor - 1;  // cAtrib, cInt, cSubtracao
    valor := valor * 2;  // cAtrib, cInt, cMultiplicacao
    valor := valor / 2;  // cAtrib, cInt, cDivisao
    if valor > 10 then  // cId, cMaior, cPalRes, cDoisPontos
      writeln('Valor é maior que 10')  // cString
    else
      writeln('Valor não é maior que 10');  // cString
  end;
  123;  // cInt
  xpt421z := xpt421z + 3;  // cId, cAdicao
  writeln('Valor de xpt421z: ', xpt421z);  // cString, cId
end.
<=

{comentario}
>=
==
>
<