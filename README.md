# MJCompiler

Compile Micro Java language specification to byte code which can be exectuted on Micro Java VM.

The language is similar to Java but reduced in complexity so it can be used for scholastic purposes.

Example program:

```
program P
  const int size = 10;
  enum Num { ZERO, ONE, TEN = 10 }
  interface I {
    int getp(int i);
    int getn(int i);
  }
  class Table implements I{
    int pos[], neg[];
    {
     void putp (int a, int idx) { this.pos[idx]=a; }
     void putn (int a, int idx) { this.neg[idx]=a; }
     int getp (int idx) { return pos[idx]; }
     int getn (int idx) { return neg[idx]; }
    }
  }
  Table val;
{
  void f(char ch, int a, int arg)
  int x;
{
  x = arg;
}
  void
  main()
  int x, i;
  char c;
  { //‐‐‐‐‐‐‐‐‐‐ Initialize val
  val = new Table;
  val.pos = new int [size];
  val.neg = new int [size];
  for (i = 0; i<size; i++)
  {
    val.putp(0,i);
    val.putn(0,i);
  }
  f(c, x, i);
  //‐‐‐‐‐‐‐‐‐‐ Read values
  read(x);
  for (;x > 0;)
  {
  if (Num.ZERO <= x && x < size)
  {
    val.putp(val.getp(x)+Num.ONE);
  } else
   if (‐size < x && x < 0)
   {
    val.putn(val.getn(‐x)+1);
   }
   read(x);
  } 
 }
}
```
