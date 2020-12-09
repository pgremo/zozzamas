{ pkgs ? import (fetchTarball "https://github.com/NixOS/nixpkgs/archive/3a02dc9edb283beb9580c9329f242ad705a721c3.tar.gz") {}
}:let

    jdk = pkgs.jdk14;
    sbt = (pkgs.sbt.override { jre = jdk; });

in pkgs.mkShell {

  buildInputs = with pkgs; [
    cachix
    git
    jdk
    powershell
    sbt
  ];

}
