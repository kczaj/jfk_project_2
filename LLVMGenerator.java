
class LLVMGenerator{
   
   static String header_text = "";
   static String main_text = "";
   static int reg = 1;

   static void printInt(String id){
      main_text += "%"+reg+" = load i32, i32* %"+id+"\n";
      reg++;
      main_text += "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strp, i32 0, i32 0), i32 %"+(reg-1)+")\n";
      reg++;
   }

   static void printReal(String id){
      main_text += "%"+reg+" = load double, double* %"+id+"\n";
      reg++;
      main_text += "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpd, i32 0, i32 0), double %"+(reg-1)+")\n";
      reg++;
   }

   static void printChar(String id){
      main_text += "%"+reg+" = load i8, i8* %"+id+"\n";
      reg++;
      main_text += "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpc, i32 0, i32 0), i8 %"+(reg-1)+")\n";
      reg++;
   }

   static void printSingleChar(String id){
      main_text += "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @strpsc, i32 0, i32 0), i8 %"+(reg-1)+")\n";
      reg++;
   }
   static void printFinalChar(String id){
      main_text += "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpc, i32 0, i32 0), i8 %"+(reg-1)+")\n";
      reg++;
   }

   static void readInt(String id){
      main_text += "%"+reg+" = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @strs, i32 0, i32 0), i32* %"+id+")\n";
      reg++;      
   }

   static void readReal(String id){
      main_text += "%"+reg+" = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strsd, i32 0, i32 0), double* %"+id+")\n";
      reg++;
   }

   static void readChar(String id){
      main_text += "%"+reg+" = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @strsc, i32 0, i32 0), i8* %"+id+")\n";
      reg++;
   }

   static void declareInt(String id){
      main_text += "%"+id+" = alloca i32\n";
   }

   static void declareReal(String id){
      main_text += "%"+id+" = alloca double\n";
   }

   static void declareChar(String id) { main_text += "%"+id+" = alloca i8\n";}

   static void declareIntArray(String id, String len){
      main_text += "%"+id+" = alloca ["+len+" x i32]\n";
   }

   static void declareRealArray(String id, String len){
      main_text += "%"+id+" = alloca ["+len+" x double]\n";
   }

   static void declareCharArray(String id, String len) {
      main_text += "%"+id+" = alloca ["+len+" x i8]\n";
   }

   static void assignInt(String id, String value){
      main_text += "store i32 "+value+", i32* %"+id+"\n";
   }

   static void assignReal(String id, String value){
      main_text += "store double "+value+", double* %"+id+"\n";
   }

   static void assignChar(String id, String value) {main_text += "store i8 "+value+", i8* %"+id+"\n";}

   static void assignArrayIntElement(String value, String arrayId, String elemId, String len) {
      main_text += "%"+reg+" = getelementptr ["+len+" x i32], ["+len+" x i32]* %"+arrayId+", i32 0, i32 "+elemId+"\n";
      main_text += "store i32 "+value+", i32* %"+reg+"\n";
      reg++;
   }

   static void assignArrayRealElement(String value, String arrayId, String elemId, String len) {
      main_text += "%"+reg+" = getelementptr ["+len+" x double], ["+len+" x double]* %"+arrayId+", i32 0, i32 "+elemId+"\n";
      main_text += "store double "+value+", double* %"+reg+"\n";
      reg++;
   }

   static void assignArrayCharElement(String value, String arrayId, String elemId, String len) {
      main_text += "%"+reg+" = getelementptr ["+len+" x i8], ["+len+" x i8]* %"+arrayId+", i32 0, i32 "+elemId+"\n";
      main_text += "store i8 "+value+", i8* %"+reg+"\n";
      reg++;
   }

   static void addInt(String val1, String val2){
      main_text += "%"+reg+" = add i32 "+val1+", "+val2+"\n";
      reg++;
   }

   static void addReal(String val1, String val2){
      main_text += "%"+reg+" = fadd double "+val1+", "+val2+"\n";
      reg++;
   }

   static void delInt(String val1, String val2){
      main_text += "%"+reg+" = sub i32 "+val1+", "+val2+"\n";
      reg++;
   }

   static void delReal(String val1, String val2){
      main_text += "%"+reg+" = fsub double "+val1+", "+val2+"\n";
      reg++;
   }

   static void multInt(String val1, String val2){
      main_text += "%"+reg+" = mul i32 "+val1+", "+val2+"\n";
      reg++;
   }

   static void multReal(String val1, String val2){
      main_text += "%"+reg+" = fmul double "+val1+", "+val2+"\n";
      reg++;
   }

   static void divInt(String val1, String val2){
      main_text += "%"+reg+" = sdiv i32 "+val1+", "+val2+"\n";
      reg++;
   }

   static void divReal(String val1, String val2){
      main_text += "%"+reg+" = fdiv double "+val1+", "+val2+"\n";
      reg++;
   }

   static int loadInt(String id){
      main_text += "%"+reg+" = load i32, i32* %"+id+"\n";
      reg++;
      return reg-1;
   }

   static int loadReal(String id){
      main_text += "%"+reg+" = load double, double* %"+id+"\n";
      reg++;
      return reg-1;
   }

   static int loadChar(String id){
      main_text += "%"+reg+" = load i8, i8* %"+id+"\n";
      reg++;
      return reg-1;
   }

   static int loadIntArrayValue(String id, String arrId, String len){
      main_text += "%"+reg+" = getelementptr ["+len+" x i32], ["+len+" x i32]* %"+id+", i32 0, i32 "+arrId+"\n";
      reg++;
      main_text += "%"+reg+" = load i32, i32* %"+(reg-1)+"\n";
      reg++;
      return reg-1;
   }

   static int loadRealArrayValue(String id, String arrId, String len){
      main_text += "%"+reg+" = getelementptr ["+len+" x double], ["+len+" x double]* %"+id+", i32 0, i32 "+arrId+"\n";
      reg++;
      main_text += "%"+reg+" = load double, double* %"+(reg-1)+"\n";
      reg++;
      return reg-1;
   }

   static int loadCharArrayValue(String id, String arrId, String len){
      main_text += "%"+reg+" = getelementptr ["+len+" x i8], ["+len+" x i8]* %"+id+", i32 0, i32 "+arrId+"\n";
      reg++;
      main_text += "%"+reg+" = load i8, i8* %"+(reg-1)+"\n";
      reg++;
      return reg-1;
   }

   static String generate(){
      String text = "";
      text += "declare i32 @printf(i8*, ...)\n";
      text += "declare i32 @__isoc99_scanf(i8*, ...)\n";
      text += "@strpc = constant [4 x i8] c\"%c\\0A\\00\"\n";
      text += "@strpsc = constant [3 x i8] c\"%c\\00\"\n";
      text += "@strp = constant [4 x i8] c\"%d\\0A\\00\"\n";
      text += "@strpd = constant [4 x i8] c\"%f\\0A\\00\"\n";
      text += "@strs = constant [3 x i8] c\"%d\\00\"\n";
      text += "@strsc = constant [3 x i8] c\"%c\\00\"\n";
      text += "@strsd = constant [4 x i8] c\"%lf\\00\"\n";
      text += header_text;
      text += "define i32 @main() nounwind{\n";
      text += main_text;
      text += "ret i32 0 }\n";
      return text;
   }

}
