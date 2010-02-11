#!/usr/bin/env ruby

module Problem8
  require 'openssl'

  SHA = OpenSSL::Digest::SHA256.new
  KEY = "\x0b" * 32
  MSG = "\x4d\x41\x43\x73\x20\x61\x72\x65" <<
        "\x20\x76\x65\x72\x79\x20\x75\x73" <<
        "\x65\x66\x75\x6c\x20\x69\x6e\x20" <<
        "\x63\x72\x79\x70\x74\x6f\x67\x72" <<
        "\x61\x70\x68\x79\x21"

  class << self
    def the_answer
      OpenSSL::HMAC.hexdigest(SHA, KEY, MSG)
    end
  end
end

__FILE__ == $0 and p Problem8.the_answer
