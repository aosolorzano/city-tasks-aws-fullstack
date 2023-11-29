## City Tasks App: An Ionic/Angular application with NgRx for reactive programming.

### Required Tools
- pnpm add -g @angular/cli
- pnpm add -g @ionic/cli
- pnpm add -g http-server
- pnpm add -g @aws-amplify/cli


### Adding Amplify dependencies
Install the Amplify dependency:
```bash
pnpm add aws-amplify
```
We are not installing the `@aws-amplify/ui-angular` package because we're not using any Amplify UI component like the Authentication.


### Adding OAuth2 dependencies
Install the OAuth2 dependencies:
```bash
pnpm add angular-oauth2-oidc
```

### Adding Angular Reactive (NgRx) dependencies
For NgRx, we need to install the following dependencies:
```
pnpm add @ngrx/store@latest && pnpm exec nx g @ngrx/store:ng-add
pnpm add @ngrx/store-devtools@latest && pnpm exec nx g @ngrx/store-devtools:ng-add
pnpm add @ngrx/effects@latest && pnpm exec nx g @ngrx/effects:ng-add
pnpm add @ngrx/router-store@latest && pnpm exec nx g @ngrx/router-store:ng-add
pnpm add @ngrx/data@latest && pnpm exec nx g @ngrx/data:ng-add
```
These commands edit the "app.module.ts" file with the required imports and initial configurations. See the blog article for more details.


### Adding the Animate CSS
[Animate.css](https://animate.style/) is a library of ready-to-use, cross-browser animations for use in your web projects.
```bash
pnpm add animate.css
```
Modify the "global.scss" file to add the following code:
```scss
@import "~animate.css/animate.min.css";
```


### Ionic DateTime
For this component, we must install the "date-fns" and "date-fn-tz" dependency for datetime validation and manipulation:
```bash
pnpm add date-fns
pnpm add date-fns-tz
```


### Other Ionic/Angular commands
First, install the Ionic/Angular Nx extension:
```bash
pnpm add --save-dev --save-exact @nxext/ionic-angular
```

Then, create the new Ionic project using the `sidemenu` template;
```bash
nx generate @nxext/ionic-angular:app  \
    --name=city-tasks-app               \
    --template=sidemenu                 \
    --unitTestRunner=jest               \
    --e2eTestRunner=cypress             \
    --linter=eslint                     \
    --capacitor=false
```
To create an Ionic **page**:
```bash
nx generate @nxext/ionic-angular:page --name=home --project=city-tasks-app --directory=pages
nx generate @nxext/ionic-angular:page --name=errors --project=city-tasks-app --directory=pages
```

To execute Angular commands, we must install the following Angular plugin generation:
```bash
pnpm add -D @nrwl/angular
```

To create a new angular **module** with a routing file;
```bash
nx g @nrwl/angular:module --name=tasks --project=city-tasks-app --routing
```

To create a new **component**:
```bash
nx g @nrwl/angular:component --name=save --directory=apps/city-tasks-app/src/app/pages/tasks/components/save --nameAndDirectoryFormat=as-provided --module=tasks.module.ts --skipTests

nx g @nrwl/angular:component --name=details --directory=apps/city-tasks-app/src/app/pages/tasks/components/details --nameAndDirectoryFormat=as-provided --module=tasks.module.ts --skipTests
```

To create a new angular **service**:
```bash
nx g @nrwl/angular:service shared/services/storage --project=city-tasks-app --skipTests
```
