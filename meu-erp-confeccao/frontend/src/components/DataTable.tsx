import {
  useTable,
  stockFeatures,
} from '@tanstack/react-table';
import type { ColumnDef, RowData } from '@tanstack/react-table';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  TablePagination
} from '@mui/material';
import React, { useState } from 'react';

interface DataTableProps<TData extends RowData> {
  columns: ColumnDef<any, TData, any>[];
  data: TData[];
  onRowClick?: (row: any) => void;
}

export function DataTable<TData extends RowData>({
  columns,
  data,
  onRowClick
}: DataTableProps<TData>) {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const paginatedData = React.useMemo(() => {
    const start = page * rowsPerPage;
    return data.slice(start, start + rowsPerPage);
  }, [data, page, rowsPerPage]);

  const table = useTable({
    data: paginatedData,
    columns,
    features: stockFeatures,
  });

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  return (
    <TableContainer component={Paper} sx={{ mt: 2, mb: 2 }}>
      <Table sx={{ minWidth: 650 }} aria-label="data table">
        <TableHead>
          {table.getHeaderGroups().map((headerGroup) => (
            <TableRow key={headerGroup.id}>
              {headerGroup.headers.map((header) => (
                <TableCell key={header.id}>
                  {header.isPlaceholder
                    ? null
                    : <table.FlexRender header={header} />}
                </TableCell>
              ))}
            </TableRow>
          ))}
        </TableHead>
        <TableBody>
          {table.getRowModel().rows.length ? (
            table.getRowModel().rows.map((row) => (
              <TableRow
                key={row.id}
                sx={{ '&:last-child td, &:last-child th': { border: 0 }, cursor: onRowClick ? 'pointer' : 'default' }}
                onClick={() => onRowClick && onRowClick(row)}
              >
                {row.getVisibleCells().map((cell) => (
                  <TableCell key={cell.id}>
                    <table.FlexRender cell={cell} />
                  </TableCell>
                ))}
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={columns.length} align="center">
                Nenhum registro encontrado.
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
      <TablePagination
        rowsPerPageOptions={[5, 10, 25]}
        component="div"
        count={data.length}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        labelRowsPerPage="Linhas por página:"
      />
    </TableContainer>
  );
}
