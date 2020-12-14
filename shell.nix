{ pkgs ? import (fetchTarball "https://github.com/NixOS/nixpkgs/archive/344652380833670dd771f51e5886a35cb45cfe55.tar.gz") {}
}:let

    jdk = pkgs.jdk14;
    sbt = (pkgs.sbt.override { jre = jdk; });

in pkgs.mkShell {

  buildInputs = with pkgs; [
    git
    jdk
    powershell
    sbt
  ];

}
