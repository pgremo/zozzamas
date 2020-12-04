{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = with pkgs; [
    git
    jdk11
    powershell
    (sbt.override {
      jre = adoptopenjdk-jre-openj9-bin-11;
    })
  ];
}
