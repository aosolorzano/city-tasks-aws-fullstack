import { AppModule } from "./app/app.module";
import { ConsoleLogger } from "aws-amplify/utils";
import { enableProdMode } from "@angular/core";
import { platformBrowserDynamic } from "@angular/platform-browser-dynamic";
import { Amplify } from "aws-amplify";
import { environment } from "./environments/environment";
import amplifyConfig from "./amplifyconfiguration.json";

Amplify.configure(amplifyConfig);

if (environment.production) {
  ConsoleLogger.LOG_LEVEL = "INFO";
  enableProdMode();
} else {
  ConsoleLogger.LOG_LEVEL = "DEBUG";
}

platformBrowserDynamic()
  .bootstrapModule(AppModule)
  .catch((err) => console.error(err));
