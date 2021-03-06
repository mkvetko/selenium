// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium.chrome;

import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

import com.google.common.io.Files;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.testing.InProject;
import org.openqa.selenium.testing.JUnit4TestBase;
import org.openqa.selenium.testing.NeedsLocalEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Functional tests for {@link ChromeOptions}.
 */
public class ChromeOptionsFunctionalTest extends JUnit4TestBase {

  private static final String EXT_PATH = "third_party/chrome_ext/backspace.crx";

  private ChromeDriver driver = null;

  @After
  public void tearDown() throws Exception {
    if (driver != null) {
      driver.quit();
    }
  }

  @NeedsLocalEnvironment
  @Test
  public void canStartChromeWithCustomOptions() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("user-agent=foo;bar");
    driver = new ChromeDriver(options);

    driver.get(pages.clickJacker);
    Object userAgent = driver.executeScript("return window.navigator.userAgent");
    assertEquals("foo;bar", userAgent);
  }

  @NeedsLocalEnvironment
  @Test
  public void optionsStayEqualAfterSerialization() throws Exception {
    ChromeOptions options1 = new ChromeOptions();
    ChromeOptions options2 = new ChromeOptions();
    assertEquals("empty chrome options should be equal", options1, options2);
    options1.asMap();
    assertEquals("empty chrome options after one is .toJson() should be equal",
               options1, options2);
  }

  @NeedsLocalEnvironment
  @Test
  public void canSetAcceptInsecureCerts() {
    ChromeOptions options = new ChromeOptions();
    options.setAcceptInsecureCerts(true);
    driver = new ChromeDriver(options);

    assertEquals(driver.getCapabilities().getCapability(CapabilityType.ACCEPT_SSL_CERTS), true);
  }

  @NeedsLocalEnvironment
  @Test
  public void canAddExtensionFromFile() {
    ChromeOptions options = new ChromeOptions();
    options.addExtensions(InProject.locate(EXT_PATH).toFile());
    driver = new ChromeDriver(options);

    driver.get(pages.clicksPage);

    driver.findElement(By.id("normal")).click();
    new WebDriverWait(driver, 10).until(titleIs("XHTML Test Page"));

    driver.findElement(By.tagName("body")).sendKeys(Keys.BACK_SPACE);
    new WebDriverWait(driver, 10).until(titleIs("clicks"));
  }

  @NeedsLocalEnvironment
  @Test
  public void canAddExtensionFromStringEncodedInBase64() throws IOException {
    ChromeOptions options = new ChromeOptions();
    options.addEncodedExtensions(Base64.getEncoder().encodeToString(
        Files.toByteArray(InProject.locate(EXT_PATH).toFile())));
    driver = new ChromeDriver(options);

    driver.get(pages.clicksPage);

    driver.findElement(By.id("normal")).click();
    new WebDriverWait(driver, 10).until(titleIs("XHTML Test Page"));

    driver.findElement(By.tagName("body")).sendKeys(Keys.BACK_SPACE);
    new WebDriverWait(driver, 10).until(titleIs("clicks"));
  }

}
