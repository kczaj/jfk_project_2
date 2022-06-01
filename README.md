# Język _Czajmal_
__Autorzy__: Jakub Czajka, Michał Malinowski
## Podręcznik użytkowania
Projekt języku został wykonany w ramach przedmiotu _Języki formalne i kompilatory_. Do wykonania projektu został wykorzystany język _Java_. Gramatyka oraz _Parser_ zostały wykonane z wykorzystaniem narzędzia _ANTLR_.

Język pozwala na tworzenie prostych programów z rozszerzeniem _cmal_. Umożliwa tworzenie zmienny, wykonywanie operacji arytmetycznych na zmiennych oraz obsługę zmiennych tablicowych.
Język posiada zaimplementowane podstawowe metody wejścia i wyjścia. Oprócz tego język umożliwia wykorzystanie instrukcji warunkowych oraz pętli. 
Utworzony przez nas język pozwala równiez na tworzenie funkcji oraz struktur. Co więcej posiada mechanizm obsługi zasięgu zmiennych (zmienne globalne oraz lokalne). 
W razie problemów język posiada rozbudowany modułu wskazywania błędów podczas analizy leksykalno-składniowej.

Język wymusza na programiście rozpoczęcie pliku od definicji struktur, następnie funkcji by później rozpocząć główną część działania programu. Składnia języka wzorowana była na języku Python stąd brak _;_ na końcach linii.
Język jednak zmusza do jawnego definiowania typów zmiennych. 

Podział kolejności bloków w programie:
```$xslt
<definicja struktury>
<definicja struktury>
...
<definicja funkcji>
<definicja funkcji>
...
<działanie programu>
```
Przykładowy kod:
```$xslt
structure Foo beginstructure
int
int
real
endstructure

real function testFun(int a, real b) begin
    print(a)
    print(b)
    real c
    c = 2.0 * b
    return c
endfunction

Foo f
int x = 2
real y = 3.5
real z
z = testFun(x, y)
print(z)
```

### Zmienne

Język wspiera 3 typy zmiennych:
- int - typ całkowity
- real - typ zmiennoprzecinkowy
- char - typ reprezentujący znaki

Język wspiera również podstawowe operacje algebraiczne:
- "\+" - dodawanie
- "\-" - odejmowanie
- "\*" - mnożenie
- "\/" - dzielenie

Możliwe są 3 główne operacje na zmiennych:
- deklaracja
- deklaracja z przypisaniem
- przypisanie

Składnia wymienionych operacji:
```$xslt
// deklaracja
<typ> <nazwa zmiennej>
// deklaracja z przypisaniem
<typ> <nazwa zmiennej> = <wartość>
// przypisanie
<nazwa zmiennej> = <wartość>
```

Przykładowy kod:
```$xslt
int z = 4
z = z + 2 * 3 - 3
real a
a = 1.1 * 1.1 + 5.4 / 2.7
```
Zmienne typu _char_ wykorzystywane są do tworzenia łańcuchów znaków. Używany jest do tego mechanizm zmiennych tablicowych.

### Zmienne tablicowe
Zmienne tablicowe wspierają podstawowe typy zmiennych. Pozwalają na inicjalizację tablicy podczas deklaracji zmiennej, wykorzystywanie pojedyńczych elementów jak i manipulacja zawartością tablicy.

Składnia zmiennych tablicowych:
```$xslt
// delkaracja
<typ>{<liczba elementów>} <nazwa zmiennej> 

// deklaracja z przypisaniem
<typ>{<liczba elementów>} <nazwa zmiennej> = [<wartość>, <wartość>, ...]
```
Przykładowy kod: 
```$xslt
int{3} arr = [1, 2, 3]
real{2} arrR = [2.1, 24.2]
```
W przypadku typu zmiennej _char_ składnia ma następującą formę:
```$xslt
// deklaracja
<typ>{<liczba elementów>} <nazwa zmiennej> 

// deklaracja z przypisaniem
<typ>{<liczba elementów>} <nazwa zmiennej> = "<łańcuch znaków>"
```
Przykładowy kod:
```$xslt
char{5} s = "hello"
```
### Funkcje wbudowane
Dostępne są 2 funkcje wbudowane:
- print() - odpowiadający za wypisywanie wartości zmiennych 
- read() - odpowiadający za wczytywanie wartości zmiennych

#### Funkcja _print_
Funkcja pozwala na wypisywanie wartości zmiennych typu _int_, _real_, _char_ oraz ich odpowiedników tablicowych (pojedyńcze elementy).

Składnia wywołania funkcji:
```$xslt
print(<zmienna wypisywana>)
``` 
#### Funkcja _read_
Funkcja pozwala na wpisywanie wartości zmiennych wprowadzony przez użytkownika typu _int_, _real_, _char_.

Składnia wywołania funkcji:
```$xslt
read(<zmienna wpisywana>)
``` 

### Instrukcja warunkowa
Instrukcja warunkowa pozwala na sterowanie przebiegiem działania programu. Operacje dozowlone w warunku to:
- == - równość
- != - nierówność
- < - mniejsze
- <= - mniejsze równe
- \> - większe
- \>= - większe równe

Składnia instrukcji warunkowej:
```$xslt
if <warunek> begin
<zachowanie dla prawdziwego warunku>
endif else
<zachowanie dla fałszywego warunku>
endelse
```
Przykładowy kod:
```
if x < 5 begin
    if x == 4 begin
        print(x)
    endif else
        int y = 5
        print(y)
    endelse
endif else
x = 4
endelse
```
### Pętla
Przykładowy kod:
```
loop x < 5 begin
    y = 0
    print(x)
    loop y < 3 begin
        print(s)
    endloop
endloop
```
### Funkcja
Przykładowy kod:
```$xslt
real function testFun(int a, real b) begin
    print(a)
    print(b)
    real c
    c = 2.0 * b
    return c
endfunction
```

### Struktura
Struktura pozwala użytkownikowi na stworzenie własnego typu. Definiowana jest na początku działania programu.
Nazwy struktur muszą być nazwami jednoznacznie określonymi. Możliwe typu dostępne w trakcie tworzenia struktur:
- int
- real

Składnia struktury:
```$xslt
structure <nazwa struktury> beginstructure
<typ zmiennej>
<typ zmiennej>
<typ zmiennej>
...
endstructure
```
Przykładowy kod:
```$xslt
structure Foo beginstructure
int
int
real
endstructure
```
Aby skorzystać z utworzonej struktury należy ją stworzyć jak dowolną inną zmienną podając jako typ nazwę struktury.
```$xslt
structure Foo beginstructure
int
int
real
endstructure

Foo f
```
W takim przypadku wartości zawarte w strukturze nie przyjeły jeszcze wartości. 
Ustawianie wartości wykonywane jest poprzez odniesienie się do indeksu pola struktury.

Składnia przypisania wartości:
```$xslt
Foo f

f.0 = 1
f.1 = 2
f.2 = 31.0
```

Wartości struktury można dowolnie edytować oraz wyciągać do zmiennych- podobnie jak wartości zmiennych tablicowych.

```$xslt
int x
Foo f
f.0 = 12
f.1 = 13
f.2 = 20.1
f.2 = 12.1
x = f.0
```
