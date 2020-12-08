{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = with pkgs; [
    git
    jdk14
    powershell
    (sbt.override {
      jre = jdk14;
    })
  ];
}
