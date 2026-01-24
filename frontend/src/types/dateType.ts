export type DateTypeId =
  | 'romantic'
  | 'activity'
  | 'food'
  | 'culture'
  | 'healing';

export interface DateType {
  id: DateTypeId;
  name: string;
  description: string;
  icon: string;
}

export interface DateTypesResponse {
  dateTypes: DateType[];
}
