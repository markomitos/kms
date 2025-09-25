import {KeyVersion} from "./keyversion.model";
import {KeyType} from "./keytype.model";

export interface Key{
  id?: string,
  alias?: string,
  type?: KeyType,
  currentVersion?: number,
  userId?: string,
  versions?: KeyVersion,
}



