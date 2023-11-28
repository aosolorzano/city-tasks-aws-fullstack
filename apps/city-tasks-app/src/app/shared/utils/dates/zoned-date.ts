// The ionChange event will emit the date value as an ISO-8601 STRING in the
// event payload. It is the developer's responsibility to format it based on
// their application needs. We recommend the use of "date-fns" to format the date value.
import { format, utcToZonedTime } from "date-fns-tz";
import { parseISO } from "date-fns";
import { environment } from "../../../../environments/environment";

export class ZonedDate {

  public static getAsString(date: Date): string {
    const zonedExecutionTime: Date = utcToZonedTime(date, environment.timeZone);
    return format(zonedExecutionTime, "yyyy-MM-dd'T'HH:mm:ssXXX", { timeZone: environment.timeZone });
  }

  public static getParsed(date: string, dateFormat?: string): string {
    return format(parseISO(date),
      dateFormat ?? "yyyy-MM-dd'T'HH:mm:ssXXX",
      { timeZone: environment.timeZone });
  }

  public static setTimeToMidnight(date: Date): Date {
    date.setHours(23, 59, 59, 0);
    return date;
  }
}
