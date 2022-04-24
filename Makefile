ANTLR=../antlr-4.10.1-complete.jar

all: generate compile test

clear:
	rm *.class
	rm -r output
generate:
	java -jar $(ANTLR) -o output Czajmal.g4
compile2:
	javac -cp $(ANTLR):output:. output/Czajmal*.java
compile:
	javac -cp $(ANTLR):output:. Main.java
test:
	java -cp $(ANTLR):output:. Main test.cmal > test.ll
	lli test.ll
