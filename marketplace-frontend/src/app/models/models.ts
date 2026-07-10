// TypeScript interfaces mirroring the Java entity DTOs

export interface User {
  userId: string;
  password: string;
}

export interface Employee {
  empId?: number;
  empName: string;
  deptName?: string;
  location?: string;
  user?: User;
}

export interface Resource {
  resId?: number;
  title: string;
  description?: string;
  category?: string;
  type?: string;
  price?: number;
  date?: string;
  emp?: Employee;
  resourceType?: string; // discriminator
}

// NOTE: the backend uses Lombok on boolean fields named isFulfilled/isAvailable/isAccepted,
// so Jackson serializes them as "fulfilled"/"available"/"accepted" on the wire.
export interface Requirement extends Resource {
  fulfilled?: boolean;
  fulfilledOn?: string;
  proposals?: Proposal[];
}

export interface Offer extends Resource {
  available?: boolean;
  availableUpto?: string;
  proposals?: Proposal[];
}

export interface Proposal {
  propId?: number;
  proposal: string;
  amount: number;
  proposalDate?: string;
  accepted?: boolean;
  acceptedOn?: string;
  emp?: Employee;
  resource?: Resource;
}

export interface ApiError {
  status: number;
  error: string;
  message: string;
  timestamp: string;
}

export type ResourceCategory = 'Accommodation' | 'Electronics' | 'Vehicle' | 'Services' | 'Other';
export type ResourceType = 'SELL' | 'RENT' | 'FREE' | 'HELP';
