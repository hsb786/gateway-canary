package com.example.gateway.canary.canary.constant;

/**
 * @author hushengbin
 * @date 2021-07-02 10:23
 */
public interface CanaryMetadataConstant {

  String VERSION = "version";

  enum VersionStatus {
    TARGET("TARGET"),
    DEFAULT("DEFAULT"),
    UN_KNOW("UN_KNOW");

    private String name;

    VersionStatus(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
