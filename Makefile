ANTLR=../antlr-4.10.1-complete.jar

all: generate compile

generate:
	java -jar $(ANTLR) -o output Czajmal.g4
compile:
	javac -cp $(ANTLR):output:. output/Czajmal*.java
