let
    nixpkgs = fetchTarball {
      url = "https://github.com/NixOS/nixpkgs/archive/3a02dc9edb283beb9580c9329f242ad705a721c3.tar.gz";
    };

    pkgs = import nixpkgs {};

    jdk = pkgs.jdk14;

in pkgs.mkShell {

  buildInputs = with pkgs; [
    git
    jdk
    powershell
    (sbt.override {
      jre = jdk;
    })
  ];

}
