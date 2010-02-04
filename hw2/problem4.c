#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <openssl/aes.h>

#define KEY ("\x80\x00\x00\x00\x00\x00\x00\x00"\
             "\x00\x00\x00\x00\x00\x00\x00\x00"\
             "\x00\x00\x00\x00\x00\x00\x00\x00"\
             "\x00\x00\x00\x00\x00\x00\x00\x01")

#define MSG ("\x53\x9b\x33\x3b\x39\x70\x6d\x14"\
             "\x90\x28\xcf\xe1\xd9\xd4\xa4\x07")

#define KEYSIZ (2 << 7)

#define BUFSIZ 1024

int main() {
  unsigned char out[BUFSIZ];

  AES_KEY aes_key;

  AES_set_decrypt_key(KEY, KEYSIZ, &aes_key);
  AES_decrypt(KEY, out, &aes_key);

  int i;
  for (i = 0; i < strlen(out); i++) {
    printf("%x ", (unsigned int)out[i]);
  }

  printf("\n");

  return EXIT_SUCCESS;
}
