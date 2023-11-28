export class StringUtils {

  public static isBlank(str: string|null): boolean {
    return str === null || str.trim().length === 0;
  }

  public static isNotBlank(str: string|null): boolean {
    return !StringUtils.isBlank(str);
  }
}
