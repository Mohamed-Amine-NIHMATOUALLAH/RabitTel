import { useQuery } from '@tanstack/react-query'
import { agencyService, planService, contractService } from '../../services'
import type { AgencyResponse, PlanResponse, ContractResponse } from '../../types'

export interface RefData {
  agencies: AgencyResponse[]
  plans: PlanResponse[]
  contracts: ContractResponse[]
}

export function useRefData(): RefData {
  const agencies = useQuery({ queryKey: ['agencies'], queryFn: () => agencyService.getAll() })
  const plans = useQuery({ queryKey: ['plans'], queryFn: () => planService.getAll() })
  const contracts = useQuery({ queryKey: ['contracts'], queryFn: () => contractService.getAll() })
  return {
    agencies: agencies.data ?? [],
    plans: plans.data ?? [],
    contracts: contracts.data ?? [],
  }
}
