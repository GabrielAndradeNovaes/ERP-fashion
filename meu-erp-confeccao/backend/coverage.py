import csv

missed_instructions = 0
covered_instructions = 0
missed_branches = 0
covered_branches = 0
missed_lines = 0
covered_lines = 0
missed_methods = 0
covered_methods = 0

with open('target/site/jacoco/jacoco.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        missed_instructions += int(row['INSTRUCTION_MISSED'])
        covered_instructions += int(row['INSTRUCTION_COVERED'])
        missed_branches += int(row['BRANCH_MISSED'])
        covered_branches += int(row['BRANCH_COVERED'])
        missed_lines += int(row['LINE_MISSED'])
        covered_lines += int(row['LINE_COVERED'])
        missed_methods += int(row['METHOD_MISSED'])
        covered_methods += int(row['METHOD_COVERED'])

total_instructions = missed_instructions + covered_instructions
instruction_cov = (covered_instructions / total_instructions) * 100 if total_instructions > 0 else 0

total_branches = missed_branches + covered_branches
branch_cov = (covered_branches / total_branches) * 100 if total_branches > 0 else 0

total_lines = missed_lines + covered_lines
line_cov = (covered_lines / total_lines) * 100 if total_lines > 0 else 0

total_methods = missed_methods + covered_methods
method_cov = (covered_methods / total_methods) * 100 if total_methods > 0 else 0

print(f"Instructions: {covered_instructions}/{total_instructions} ({instruction_cov:.2f}%)")
print(f"Branches: {covered_branches}/{total_branches} ({branch_cov:.2f}%)")
print(f"Lines: {covered_lines}/{total_lines} ({line_cov:.2f}%)")
print(f"Methods: {covered_methods}/{total_methods} ({method_cov:.2f}%)")
