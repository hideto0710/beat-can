package com.github.hideto0710.beat_can

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.gargoylesoftware.htmlunit.BrowserVersion
import org.openqa.selenium.{JavascriptExecutor, UnhandledAlertException}
import org.openqa.selenium.htmlunit.HtmlUnitDriver

object Beat extends RequestHandler[BeatRequest, Response] {
  implicit private val driver = new HtmlUnitDriver(BrowserVersion.CHROME)
  driver.setJavascriptEnabled(true)
  private val Url = "https://ssl.jobcan.jp/login/pc-employee/"

  def main(args: Array[String]): Unit = {
    println(exec(Attend, "client", "user", "password"))
  }

  override def handleRequest(request: BeatRequest, context: Context): Response = {
    val res = BeatRequestStatus.fromId(request.status) match {
      case Attend => exec(Attend, request.client, request.user, request.password)
      case Leave => exec(Leave, request.client, request.user, request.password)
      case _ => BeatResponse(BadRequest, s"Inputted ${request.status} not supported.")
    }
    if (res.status != Success.code) throw new Exception(s"${res.status}: ${res.detail}")
    Response(Map("detail" -> res.detail), res.status)
  }

  private def exec(status: BeatRequestStatus, client: String, user: String, password: String): BeatResponse = {
    if (login(client, user, password)) {
      println("Page title is: " + driver.getTitle)
      val status_id = driver.asInstanceOf[JavascriptExecutor]
        .executeScript("return current_status;")
        .toString
      val gripperStatus = CanStatus.fromStatusId(status_id)
      (status, gripperStatus) match {
        case (Attend, NotYet) =>
          clickBtn("adit-button-push")
          BeatResponse(Success)
        case (Leave, Working) =>
          clickBtn("adit-button-push")
          BeatResponse(Success)
        case (s, gs) =>
          BeatResponse(BadRequest, s"Try changing to $s, but status_id is $status_id.")
      }
    } else {
      BeatResponse(Unauthorized, s"Could not login.")
    }
  }

  private def login(client: String, user: String, pwd: String): Boolean = {
    println("start login.")
    driver.get(Url)
    println("Page title is: " + driver.getTitle)
    val inputClient = driver.findElementById("client_id")
    inputClient.sendKeys(client)
    val inputUser = driver.findElementById("email")
    inputUser.sendKeys(user)
    val inputPwd = driver.findElementById("password")
    inputPwd.sendKeys(pwd)
    inputPwd.submit()
    driver.getCurrentUrl.endsWith("employee")
  }

  private def clickBtn(btnId: String) = {
    val btn = driver.findElementById(btnId)
    try {
      btn.click()
    } catch {
      case e: UnhandledAlertException =>
        driver.switchTo.alert.accept()
    }
  }
}
