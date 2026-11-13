## 本地阿里云 OSS 配置

开发环境会自动读取 `sky-server/src/main/resources/application-local.properties`，该文件已被 Git 忽略。

首次使用时，将同目录的 `application-local.properties.example` 复制为 `application-local.properties`，填入自己的 AccessKey ID 和 AccessKey Secret，再运行 `SkyApplication`。

也可以设置 `ALIYUN_ACCESS_KEY_ID` 和 `ALIYUN_ACCESS_KEY_SECRET` 环境变量；环境变量优先于本地配置文件。

真实密钥只保存在本机，不要写回已纳入 Git 管理的 YAML、示例文件或文档中。
