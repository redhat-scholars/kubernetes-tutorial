/*jshint esversion: 6 */

import { series, watch } from "gulp";
import { remove } from "fs-extra";
import { readFileSync } from "fs";
import {load as yamlLoad} from "yaml-js";
import generator from "@antora/site-generator-default";
import browserSync from "browser-sync";

const filename = "dev-site.yml";
const server = browserSync.create();
const args = ["--playbook", filename];

//Watch Paths
function watchGlobs() {
  let json_content = readFileSync(`${__dirname}/${filename}`, "UTF-8");
  let yaml_content = yamlLoad(json_content);
  let dirs = yaml_content.content.sources.map(source => [
    `${source.url}/**/**.yml`,
    `${source.url}/**/**.adoc`,
    `${source.url}/**/**.hbs`
  ]); 
  dirs.push(["dev-site.yml"]);
  dirs = [].concat(...dirs);
  //console.log(dirs);
  return dirs;
}

const siteWatch = () => watch(watchGlobs(), series(build, reload));

const removeSite = done => remove("gh-pages", done);
const removeCache = done => remove(".cache", done);

function build(done) {
  generator(args, process.env)
    .then(() => {
      done();
    })
    .catch(err => {
      console.log(err);
      done();
    });
}

function workshopSite(done){
  generator(["--pull", "--stacktrace","--playbook","workshop-site.yaml"], process.env)
    .then(() => {
      done();
    })
    .catch(err => {
      console.log(err);
      done();
    });
}

function reload(done) {
  server.reload();
  done();
}

function serve(done) {
  server.init({
    server: {
      baseDir: "./gh-pages"
    }
  });
  done();
}

const _build = build;
export { _build as build };
const _clean = series(removeSite, removeCache);
export { _clean as clean };
const _default = series(_clean, build, serve, siteWatch);
export { _default as default };
//build workshop docs
const _wsite = series(_clean, workshopSite);
export { _wsite as workshopSite };