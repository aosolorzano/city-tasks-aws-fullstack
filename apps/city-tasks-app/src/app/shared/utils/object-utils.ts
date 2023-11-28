export class ObjectUtils {

  public static isNull(object: unknown): boolean {
    return object == null;
  }

  public static isNotNull(object: unknown): boolean {
    return object != null;
  }
}
