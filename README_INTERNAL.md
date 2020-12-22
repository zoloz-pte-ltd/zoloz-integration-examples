# 内部说明
zoloz bizServer example仓库，现已经和开源github地址
https://github.com/zoloz-pte-ltd/zoloz-integration-examples
进行了aci流水线集成。

流水线会在合并master后触发，触发后，会自动同步最新的仓库中的代码以及对应的git history. (force push)
使用force push的目的是保证开源仓库的代码提交历史完全与antcode仓库一致，
因此开源仓库的任何代码同步都**必须通过流水线同步，不允许手动同步**

流水线的触发需要矩诗，鸿书，蘭琪确认。

开源发布会去掉aci文件以及这个内部readme。

流水线详情请自行阅读aci代码。

ref:
https://stackoverflow.com/questions/43567577/what-is-the-different-between-force-push-and-normal-push-in-git