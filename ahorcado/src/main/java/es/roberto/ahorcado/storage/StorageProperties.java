package es.roberto.ahorcado.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties("storage") // prefix = "storage" se ha modificado por que no funcionaba
public class StorageProperties {

  private String location = "uploads"; // "upload-dir" se ha modificado porque da error

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
