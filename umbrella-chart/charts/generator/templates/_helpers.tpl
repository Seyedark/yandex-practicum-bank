{{/*
Expand the name of the chart.
*/}}
{{- define "generator.name" -}}
{{- default .Chart.Name  }}
{{- end }}