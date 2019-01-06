/*
SQLyog Professional v12.09 (64 bit)
MySQL - 10.1.29-MariaDB : Database - bm_server_export
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`bm_server_export` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `bm_server_export`;

/*Table structure for table `card` */

DROP TABLE IF EXISTS `card`;

CREATE TABLE `card` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `cardTypeId` int(11) NOT NULL,
  `template` int(2) NOT NULL,
  `cardHolder` varchar(255) DEFAULT NULL,
  `issueDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `validTill` varchar(255) NOT NULL,
  `PIN` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `publicKey` varchar(255) NOT NULL,
  `public_key_hashed` varchar(255) NOT NULL,
  `privateKey` varchar(255) NOT NULL,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `card` */

/*Table structure for table `cardtype` */

DROP TABLE IF EXISTS `cardtype`;

CREATE TABLE `cardtype` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `payment` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `cardtype` */

insert  into `cardtype`(`id`,`payment`) values (1,0),(2,1);

/*Table structure for table `cardtype_description` */

DROP TABLE IF EXISTS `cardtype_description`;

CREATE TABLE `cardtype_description` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cardTypeId` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `locale` varchar(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `cardtype_description` */

insert  into `cardtype_description`(`id`,`cardTypeId`,`title`,`locale`) values (1,1,'DEC','en'),(2,2,'BGT DIGITAL','en'),(3,1,'DEC','ru'),(4,2,'BGT DIGITAL','ru');

/*Table structure for table `emailtemplate` */

DROP TABLE IF EXISTS `emailtemplate`;

CREATE TABLE `emailtemplate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `subject` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `locale` varchar(2) NOT NULL,
  `type` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `emailtemplate` */

insert  into `emailtemplate`(`id`,`subject`,`message`,`locale`,`type`) values (1,'BGX Email Verification','<div style=\"text-align: center;\">\r\n        <p><img src=\"<img_url>bgx_logo_dark.png\" alt=\"BGX\" /></p>\r\n        <p>Dear User,</p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p>You have successfully registered on the BGX Mobile Store.</p>\r\n        <p>You are now a part of the ever growing universe of digital goods and services. Welcome!</p>\r\n        <p><a href=\"<href>\" style=\"color:#000000;font-weight:bold;\">Press Here to Confirm Your Email and Activate the Account</a></p>\r\n        <p>or copy the link from below:</p>\r\n        <p><link></p>\r\n	<p style=\"color:#ff0000;\">The activation link will be valid till <strong><valid_till> (UTC)</strong></p>\r\n        <p style=\"margin-top:40px;\">Use your login and password to access the app. Once there, create your BGT Card and enjoy the possibilities of truly decentralized shopping!</p>\r\n        <p>Sincerely,</p>\r\n        <p>The BGX Team</p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p style=\"margin-top:40px;font-style:italic;\">Having trouble? No problem. Click <a href=\"mailto:support@bgx.ai\">here</a> to contact customer support</p>\r\n        <table style=\"background-color:#333333;color:#ffffff;width: 100%; margin-top:30px;padding-top: 50px;\">\r\n      <tr>\r\n	<td></td>\r\n        <td>\r\n          <table style=\"background-color:#333333;color:#ffffff;width: 600px;margin: auto; text-align: center;\" align=\"center\">\r\n            <tr>\r\n              <td>\r\n                  <a href=\"https://t.me/bgx_group\" style=\"color: #ffffff;text-decoration: none; \">\r\n                    <img src=\"<img_url>telegram.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n		    &nbsp;&nbsp;&nbsp;Telegram\r\n		  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://twitter.com/BGXGlobal\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>twitter.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\" />\r\n                    &nbsp;&nbsp;&nbsp;Twitter\r\n                  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.facebook.com/BGXWorld/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>facebook.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Facebook\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.linkedin.com/company/27242603/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>linkedin.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;LinkedIn\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://bgx.ai/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>website.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Website\r\n                  </a>\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"5\">\r\n                  <hr style=\"border-top: solid #505050 1.5pt; margin:50px 0px;width:100%;\" />\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">Copyright &copy; <span style=\"background: #1b1a2a;\">2018 BGX Group. All rights reserved</span></p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">You are receiving this email because you registered for the BGX Mobile Store.</p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">\r\n                        Want to change how you receive these emails?<br/>\r\n                        You can <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">update your preferences</span></a> and <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">unsubscribe from this list</span></a>.\r\n                      </p>\r\n                </td>\r\n            </tr>\r\n          </table>\r\n        </td>\r\n	<td></td>\r\n      </tr>\r\n    </table>\r\n    </div>','en','active'),(2,'BGX Password Reset Confirmation','<div style=\"text-align: center;\">\r\n        <p><img src=\"<img_url>bgx_logo_dark.png\" alt=\"BGX\" /></p>\r\n        <p>Dear User,</p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p>If you requested to reset your BGX Account password, please follow the link below. </p>\r\n        <p><a href=\"<href>\" style=\"color:#000000;font-weight:bold;\">Click Here to Reset Your Password</a></p>\r\n        <p>or copy the following link:</p>\r\n        <p><link></p>\r\n	<p style=\"color:#ff0000;\">The reset link will be valid till <strong><valid_till> (UTC)</strong></p>\r\n        <p style=\"margin-top:40px;\">If you did not request a password reset then please disregard this email.</p>\r\n        <p>Sincerely,</p>\r\n        <p>The BGX Team</p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p style=\"margin-top:40px;font-style:italic;\">Having trouble? No problem. Click <a href=\"mailto:support@bgx.ai\">here</a> to contact customer support</p>\r\n        <table style=\"background-color:#333333;color:#ffffff;width: 100%; margin-top:30px;padding-top: 50px;\">\r\n      <tr>\r\n	<td></td>\r\n        <td>\r\n          <table style=\"background-color:#333333;color:#ffffff;width: 600px;margin: auto; text-align: center;\" align=\"center\">\r\n            <tr>\r\n              <td>\r\n                  <a href=\"https://t.me/bgx_group\" style=\"color: #ffffff;text-decoration: none; \">\r\n                    <img src=\"<img_url>telegram.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n		    &nbsp;&nbsp;&nbsp;Telegram\r\n		  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://twitter.com/BGXGlobal\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>twitter.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\" />\r\n                    &nbsp;&nbsp;&nbsp;Twitter\r\n                  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.facebook.com/BGXWorld/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>facebook.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Facebook\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.linkedin.com/company/27242603/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>linkedin.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;LinkedIn\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://bgx.ai/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>website.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Website\r\n                  </a>\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"5\">\r\n                  <hr style=\"border-top: solid #505050 1.5pt; margin:50px 0px;width:100%;\" />\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">Copyright &copy; <span style=\"background: #1b1a2a;\">2018 BGX Group. All rights reserved</span></p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">You are receiving this email because you registered for the BGX Mobile Store.</p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">\r\n                        Want to change how you receive these emails?<br/>\r\n                        You can <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">update your preferences</span></a> and <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">unsubscribe from this list</span></a>.\r\n                      </p>\r\n                </td>\r\n            </tr>\r\n          </table>\r\n        </td>\r\n	<td></td>\r\n      </tr>\r\n    </table>\r\n    </div>','en','resetpass'),(3,'BGX - Верификация e-mail','<div style=\"text-align: center;\">\r\n        <p><img src=\"<img_url>bgx_logo_dark.png\" alt=\"BGX\" /></p>\r\n        <p>Дорогой пользователь,</p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p>Вы успешно зарегистрировались в BGX Mobile Store.</p>\r\n        <p>Вы стали частью растущего мира цифровых товаров и сервисов! Добро пожаловать!</p>\r\n        <p><a href=\"<href>\" style=\"color:#000000;font-weight:bold;\">Нажмите ЗДЕСЬ чтобы подтвердить Ваш e-mail и активировать аккаунт</a></p>\r\n        <p>or copy the link from below:</p>\r\n        <p><link></p>\r\n	<p style=\"color:#ff0000;\">Ссылка активации будет доступна до <strong><valid_till> (UTC)</strong></p>\r\n        <p style=\"margin-top:40px;\">Используйте свой логин и пароль, чтобы доступиться до приложения. Затем создайте свою BGT карточку и наслаждайтесь возможностями децентрализованного шоппинга!</p>\r\n        <p>Искренне,</p>\r\n        <p>Команда BGX</p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p style=\"margin-top:40px;font-style:italic;\">Остались вопросы? Свяжитесь со <a href=\"mailto:support@bgx.ai\">службой</a> поддержки</p>\r\n        <table style=\"background-color:#333333;color:#ffffff;width: 100%; margin-top:30px;padding-top: 50px;\">\r\n      <tr>\r\n	<td></td>\r\n        <td>\r\n          <table style=\"background-color:#333333;color:#ffffff;width: 600px;margin: auto; text-align: center;\" align=\"center\">\r\n            <tr>\r\n              <td>\r\n                  <a href=\"https://t.me/bgx_group\" style=\"color: #ffffff;text-decoration: none; \">\r\n                    <img src=\"<img_url>telegram.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n		    &nbsp;&nbsp;&nbsp;Telegram\r\n		  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://twitter.com/BGXGlobal\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>twitter.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\" />\r\n                    &nbsp;&nbsp;&nbsp;Twitter\r\n                  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.facebook.com/BGXWorld/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>facebook.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Facebook\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.linkedin.com/company/27242603/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>linkedin.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;LinkedIn\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://bgx.ai/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>website.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Website\r\n                  </a>\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"5\">\r\n                  <hr style=\"border-top: solid #505050 1.5pt; margin:50px 0px;width:100%;\" />\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">Копирайт &copy; <span style=\"background: #1b1a2a;\">2018 BGX Group. Все права зарезервированы</span></p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">Вы получили это сообщение, потому-то Вы зарегистрированы в BGX Mobile Store.</p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">\r\n                        Хотите изменить настройки получения подобных сообщений?<br/>\r\n                        Вы можете <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">изменить настройки</span></a> или <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">отписаться от этой рассылки</span></a>.\r\n                      </p>\r\n                </td>\r\n            </tr>\r\n          </table>\r\n        </td>\r\n	<td></td>\r\n      </tr>\r\n    </table>\r\n    </div>','ru','active'),(4,'BGX - сброс пароля','<div style=\"text-align: center;\">\r\n        <p><img src=\"<img_url>bgx_logo_dark.png\" alt=\"BGX\" /></p>\r\n        <p>Дорогой пользователь,</p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p>Если Вы решили сбросить Ваш текущий пароль, пожалуйста, нажмите на ссылку внизу.</p>\r\n        <p><a href=\"<href>\" style=\"color:#000000;font-weight:bold;\">Нажмите здесь, чтобы переустановить Ваш пароль</a></p>\r\n        <p>или скопируйте следующую ссылку:</p>\r\n        <p><link></p>\r\n	<p style=\"color:#ff0000;\">Ссылка активации будет доступна до <strong><valid_till> (UTC)</strong></p>\r\n        <p style=\"margin-top:40px;\">Если Вы не требовали сбороса пароля, просто игнорируйте это сообщение.</p>\r\n        <p>Искренне,</p>\r\n        <p>Команда BGX</p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p style=\"margin-top:40px;font-style:italic;\">Остались вопросы? Свяжитесь со <a href=\"mailto:support@bgx.ai\">службой</a> поддержки</p>\r\n        <table style=\"background-color:#333333;color:#ffffff;width: 100%; margin-top:30px;padding-top: 50px;\">\r\n      <tr>\r\n	<td></td>\r\n        <td>\r\n          <table style=\"background-color:#333333;color:#ffffff;width: 600px;margin: auto; text-align: center;\" align=\"center\">\r\n            <tr>\r\n              <td>\r\n                  <a href=\"https://t.me/bgx_group\" style=\"color: #ffffff;text-decoration: none; \">\r\n                    <img src=\"<img_url>telegram.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n		    &nbsp;&nbsp;&nbsp;Telegram\r\n		  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://twitter.com/BGXGlobal\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>twitter.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\" />\r\n                    &nbsp;&nbsp;&nbsp;Twitter\r\n                  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.facebook.com/BGXWorld/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>facebook.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Facebook\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.linkedin.com/company/27242603/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>linkedin.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;LinkedIn\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://bgx.ai/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>website.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Website\r\n                  </a>\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"5\">\r\n                  <hr style=\"border-top: solid #505050 1.5pt; margin:50px 0px;width:100%;\" />\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">Копирайт &copy; <span style=\"background: #1b1a2a;\">2018 BGX Group. Все права зарезервированы</span></p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">Вы получили это сообщение, потому-то Вы зарегистрированы в BGX Mobile Store.</p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">\r\n                        Хотите изменить настройки получения подобных сообщений?<br/>\r\n                        Вы можете <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">изменить настройки</span></a> или <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">отписаться от этой рассылки</span></a>.\r\n                      </p>\r\n                </td>\r\n            </tr>\r\n          </table>\r\n        </td>\r\n	<td></td>\r\n      </tr>\r\n    </table>\r\n    </div>','ru','resetpass'),(5,'BGX Payment successful','<div style=\"text-align: center;\">\r\n        <p><img src=\"<img_url>bgx_logo_dark.png\" alt=\"BGX\" /></p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p><strong>No receipt available.</strong></p>\r\n        <p>Sincerely,</p>\r\n        <p>The BGX Team</p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p style=\"margin-top:40px;font-style:italic;\">Having trouble? No problem. Click <a href=\"mailto:support@bgx.ai\">here</a> to contact customer support</p>\r\n        <table style=\"background-color:#333333;color:#ffffff;width: 100%; margin-top:30px;padding-top: 50px;\">\r\n      <tr>\r\n	<td></td>\r\n        <td>\r\n          <table style=\"background-color:#333333;color:#ffffff;width: 600px;margin: auto; text-align: center;\" align=\"center\">\r\n            <tr>\r\n              <td>\r\n                  <a href=\"https://t.me/bgx_group\" style=\"color: #ffffff;text-decoration: none; \">\r\n                    <img src=\"<img_url>telegram.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n		    &nbsp;&nbsp;&nbsp;Telegram\r\n		  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://twitter.com/BGXGlobal\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>twitter.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\" />\r\n                    &nbsp;&nbsp;&nbsp;Twitter\r\n                  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.facebook.com/BGXWorld/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>facebook.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Facebook\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.linkedin.com/company/27242603/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>linkedin.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;LinkedIn\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://bgx.ai/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>website.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Website\r\n                  </a>\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"5\">\r\n                  <hr style=\"border-top: solid #505050 1.5pt; margin:50px 0px;width:100%;\" />\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">Copyright &copy; <span style=\"background: #1b1a2a;\">2018 BGX Group. All rights reserved</span></p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">You are receiving this email because you registered for the BGX Mobile Store.</p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">\r\n                        Want to change how you receive these emails?<br/>\r\n                        You can <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">update your preferences</span></a> and <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">unsubscribe from this list</span></a>.\r\n                      </p>\r\n                </td>\r\n            </tr>\r\n          </table>\r\n        </td>\r\n	<td></td>\r\n      </tr>\r\n    </table>\r\n    </div>','en','payment'),(6,'Оплата BGX прошла успешно','<div style=\"text-align: center;\">\r\n        <p><img src=\"<img_url>bgx_logo_dark.png\" alt=\"BGX\" /></p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p><strong>Чек недоступен.</strong></p>\r\n        <p>Искренне,</p>\r\n        <p>Команда BGX</p>\r\n        <p style=\"border-bottom: 1px dashed;max-width: 600px;margin: auto;\"></p>\r\n        <p style=\"margin-top:40px;font-style:italic;\">Остались вопросы? Свяжитесь со <a href=\"mailto:support@bgx.ai\">службой</a> поддержки</p>\r\n        <table style=\"background-color:#333333;color:#ffffff;width: 100%; margin-top:30px;padding-top: 50px;\">\r\n      <tr>\r\n	<td></td>\r\n        <td>\r\n          <table style=\"background-color:#333333;color:#ffffff;width: 600px;margin: auto; text-align: center;\" align=\"center\">\r\n            <tr>\r\n              <td>\r\n                  <a href=\"https://t.me/bgx_group\" style=\"color: #ffffff;text-decoration: none; \">\r\n                    <img src=\"<img_url>telegram.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n		    &nbsp;&nbsp;&nbsp;Telegram\r\n		  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://twitter.com/BGXGlobal\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>twitter.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\" />\r\n                    &nbsp;&nbsp;&nbsp;Twitter\r\n                  </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.facebook.com/BGXWorld/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>facebook.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Facebook\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://www.linkedin.com/company/27242603/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>linkedin.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;LinkedIn\r\n                        </a>\r\n              </td>\r\n              <td>\r\n                  <a href=\"https://bgx.ai/\" style=\"color: #ffffff;text-decoration: none;\">\r\n                    <img src=\"<img_url>website.png\" alt=\"\" style=\"vertical-align: middle;\" align=\"middle\"/>\r\n                    &nbsp;&nbsp;&nbsp;Website\r\n                  </a>\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"5\">\r\n                  <hr style=\"border-top: solid #505050 1.5pt; margin:50px 0px;width:100%;\" />\r\n              </td>\r\n            </tr>\r\n            <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">Копирайт &copy; <span style=\"background: #1b1a2a;\">2018 BGX Group. Все права зарезервированы</span></p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">Вы получили это сообщение, потому-то Вы зарегистрированы в BGX Mobile Store.</p>\r\n                </td>\r\n              </tr>\r\n              <tr>\r\n                <td colspan=\"5\">\r\n                    <p style=\"font-size: 12px;margin: 0px 0px 10px;\">\r\n                        Хотите изменить настройки получения подобных сообщений?<br/>\r\n                        Вы можете <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">изменить настройки</span></a> или <a href=\"https://ico.bgx.ai/Account/Login\" style=\"color: #ffffff;\"><span style=\"color:#ffffff\">отписаться от этой рассылки</span></a>.\r\n                      </p>\r\n                </td>\r\n            </tr>\r\n          </table>\r\n        </td>\r\n	<td></td>\r\n      </tr>\r\n    </table>\r\n    </div>','ru','payment');

/*Table structure for table `language` */

DROP TABLE IF EXISTS `language`;

CREATE TABLE `language` (
  `title` varchar(255) NOT NULL,
  `locale` varchar(2) NOT NULL,
  `image` varchar(255) NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`locale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `language` */

insert  into `language`(`title`,`locale`,`image`,`active`) values ('русский','ru','',1);

/*Table structure for table `shopping_cart` */

DROP TABLE IF EXISTS `shopping_cart`;

CREATE TABLE `shopping_cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `productId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `shopping_cart` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `ethereumAddress` varchar(42) DEFAULT NULL,
  `BGXAccount` varchar(126) DEFAULT NULL,
  `bgtAddress` varchar(255) NOT NULL,
  `hash` varchar(32) DEFAULT NULL,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` tinyint(1) NOT NULL,
  `createdLink` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `resetedLink` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `resetHash` varchar(255) NOT NULL,
  `isReseted` tinyint(1) NOT NULL,
  `magentoId` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user` */

/*Table structure for table `user_token` */

DROP TABLE IF EXISTS `user_token`;

CREATE TABLE `user_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `token` varchar(255) NOT NULL,
  `deviceType` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user_token` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
